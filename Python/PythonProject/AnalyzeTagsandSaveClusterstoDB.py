from transformers import BertTokenizer, BertForSequenceClassification
import pandas as pd
from transformers import BertModel, BertTokenizer, BertForSequenceClassification, Trainer, TrainingArguments
import torch
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import LabelEncoder
from sqlalchemy import create_engine
import re

# 创建数据库连接引擎
engine = create_engine('mysql+pymysql://root:1234@localhost/db02')

# 读取数据，只处理 video_cluster_id 为 0 的视频
data = pd.read_sql("SELECT video_id, video_title, video_tags FROM videos WHERE video_cluster_id = 0", engine)

# 检查数据是否为空
if data.empty:
    print("No data found with video_cluster_id = 0")
else:
    def preprocess_tags(tags):
        """预处理标签，将具体标签归类到大类别，并处理包含大聚类关键词的标签"""
        tag_map = {
            "反恐精英": "游戏", "射击": "游戏", "生活": "生活",
            # 添加更多映射规则...
        }
        # 定义大聚类关键词
        large_keywords = {
            "游戏": ["游戏", "手机游戏", "电脑游戏", "射击游戏", "动作游戏", "策略游戏"],
            "动画": ["番剧", "动漫", "动画"],
            # 添加其他大聚类关键词...
        }

        tags = tags.split(',')
        processed_tags = []

        for tag in tags:
            # 检查标签是否包含大聚类关键词或包含 "游戏" 二字
            for large_category, keywords in large_keywords.items():
                if any(keyword in tag for keyword in keywords):
                    processed_tags.append(large_category)
                    break
                elif re.search(r'游戏', tag):  # 检查标签中是否包含 "游戏" 二字
                    processed_tags.append('游戏')
                    break
            else:
                # 如果标签不包含大聚类关键词或 "游戏" 二字，则根据映射规则处理
                processed_tags.append(tag_map.get(tag, tag))

        return ' '.join(processed_tags)


    # 应用标签预处理
    data['processed_tags'] = data['video_tags'].apply(preprocess_tags)

    # 将类别标签转换为数字编码
    label_encoder = LabelEncoder()
    data['labels'] = label_encoder.fit_transform(data['processed_tags'])

    # 检查数据是否为空
    if data.empty:
        print("No data available for training and validation")
# 指定检查点路径
checkpoint_dir = './results/checkpoint-17328'

# 加载检查点的分词器和模型
tokenizer = BertTokenizer.from_pretrained(checkpoint_dir)
model = BertForSequenceClassification.from_pretrained(checkpoint_dir)

# 切换模型到推理模式（如果是预测阶段）
model.eval()

# 将模型移到 GPU 或 CPU
import torch
device = torch.device('cuda') if torch.cuda.is_available() else torch.device('cpu')
model.to(device)

print("Checkpoint successfully loaded from", checkpoint_dir)

# 创建一个新的 PyTorch 数据集
class VideoDataset(torch.utils.data.Dataset):
    def __init__(self, encodings):
        self.encodings = encodings

    def __getitem__(self, idx):
        item = {key: torch.tensor(val[idx]) for key, val in self.encodings.items()}
        return item

    def __len__(self):
        return len(self.encodings['input_ids'])

# 对要预测的数据进行分词
test_texts = data['processed_tags'].tolist()  # 替换为实际要预测的文本
encodings = tokenizer(test_texts, truncation=True, padding=True, max_length=128)
dataset = VideoDataset(encodings)

# 使用加载的模型进行预测
from transformers import Trainer

# 创建一个 Trainer 实例（加载已保存的模型参数）
trainer = Trainer(model=model)

# 执行预测
predictions = trainer.predict(dataset)
preds = torch.argmax(torch.tensor(predictions.predictions), dim=1).tolist()

# 打印预测结果
print("Predicted labels:", preds)

# 更新数据库中的 video_cluster_id 列
data['video_cluster_id'] = preds
engine = create_engine('mysql+pymysql://root:1234@localhost/db02')
connection = engine.raw_connection()
try:
    with connection.cursor() as cursor:
        for index, row in data.iterrows():
            sql = """
            UPDATE videos
            SET video_cluster_id = %s
            WHERE video_id = %s
            """
            cursor.execute(sql, (row['video_cluster_id'], row['video_id']))
        connection.commit()
        print("Cluster results saved to database.")
except Exception as e:
    print("Failed to save clusters to database:", e)
finally:
    connection.close()

