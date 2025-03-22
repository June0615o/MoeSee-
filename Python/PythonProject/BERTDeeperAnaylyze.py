import pandas as pd
from transformers import BertTokenizer, BertForSequenceClassification, Trainer
import torch
from sqlalchemy import create_engine

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
                elif "游戏" in tag:  # 检查标签中是否包含 "游戏" 二字
                    processed_tags.append('游戏')
                    break
            else:
                # 如果标签不包含大聚类关键词或 "游戏" 二字，则根据映射规则处理
                processed_tags.append(tag_map.get(tag, tag))

        return ' '.join(processed_tags)


    # 应用标签预处理
    data['processed_tags'] = data['video_tags'].apply(preprocess_tags)

    # 加载最新的检查点（如 checkpoint-17328）
    checkpoint_dir = './results/checkpoint-17328'
    tokenizer = BertTokenizer.from_pretrained('bert-base-chinese')
    model = BertForSequenceClassification.from_pretrained(checkpoint_dir)

    # 将模型移到 GPU 或 CPU
    device = torch.device('cuda') if torch.cuda.is_available() else torch.device('cpu')
    model.to(device)

    # 创建 Trainer 实例
    trainer = Trainer(model=model)

    # 分批处理
    batch_size = 100  # 每批次处理的样本数量
    num_batches = len(data) // batch_size + 1

    for batch_idx in range(num_batches):
        # 提取当前批次数据
        batch_data = data.iloc[batch_idx * batch_size:(batch_idx + 1) * batch_size]
        if batch_data.empty:
            continue

        # 对当前批次进行分词
        encodings = tokenizer(batch_data['processed_tags'].tolist(), truncation=True, padding=True, max_length=128)


        class VideoDataset(torch.utils.data.Dataset):
            def __init__(self, encodings):
                self.encodings = encodings

            def __getitem__(self, idx):
                item = {key: torch.tensor(val[idx]) for key, val in self.encodings.items()}
                return item

            def __len__(self):
                return len(self.encodings['input_ids'])


        dataset = VideoDataset(encodings)

        # 执行预测
        predictions = trainer.predict(dataset)
        preds = torch.argmax(torch.tensor(predictions.predictions), dim=1).tolist()

        # 将当前批次的预测结果写入数据库
        batch_data['video_cluster_id'] = preds
        try:
            connection = engine.raw_connection()
            with connection.cursor() as cursor:
                for index, row in batch_data.iterrows():
                    sql = """
                    UPDATE videos
                    SET video_cluster_id = %s
                    WHERE video_id = %s
                    """
                    cursor.execute(sql, (row['video_cluster_id'], row['video_id']))
                connection.commit()
                print(f"Batch {batch_idx + 1}/{num_batches} processed and saved to database.")
        except Exception as e:
            print(f"Failed to save batch {batch_idx + 1} to database:", e)
        finally:
            connection.close()
