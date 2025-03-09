import pandas as pd
import matplotlib.pyplot as plt
from sqlalchemy import create_engine
from collections import Counter
import matplotlib

# 设置字体为 SimHei（黑体）以支持中文显示
plt.rcParams['font.sans-serif'] = ['SimHei']
plt.rcParams['axes.unicode_minus'] = False  # 正常显示负号

# 创建数据库连接引擎
engine = create_engine('mysql+pymysql://root:1234@localhost/db02')

# 从数据库中读取视频标签数据
data = pd.read_sql("SELECT video_tags FROM videos", engine)

# 大聚类标签列表
large_categories = ["综艺", "动画", "鬼畜", "舞蹈", "娱乐", "科技数码", "美食", "汽车",
                    "体育运动", "电影", "电视剧", "纪录片", "游戏", "音乐", "影视", "知识",
                    "资讯", "小剧场", "时尚美妆", "动物", "vlog", "绘画", "人工智能",
                    "家装房产", "户外", "健身", "手工", "旅游出行", "三农", "亲子", "健康",
                    "情感", "生活兴趣", "生活经验", "公益","必剪创作","生活"]

# 分割标签并统计出现次数
all_tags = []
for tags in data['video_tags']:
    all_tags.extend(tags.split(','))

# 筛除大聚类词
filtered_tags = [tag for tag in all_tags if tag not in large_categories]

tag_counts = Counter(filtered_tags)

# 选择出现次数较高的标签进行绘制
most_common_tags = tag_counts.most_common(10)  # 可调整显示的标签数量
labels, counts = zip(*most_common_tags)

# 绘制饼图
plt.figure(figsize=(10, 6))
plt.pie(counts, labels=labels, autopct='%1.1f%%', startangle=140)
plt.title('视频标签分布（筛除大聚类词）')
plt.axis('equal')  # Equal aspect ratio ensures that pie is drawn as a circle.
plt.show()
