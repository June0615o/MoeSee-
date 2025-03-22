import pandas as pd
from sqlalchemy import create_engine

# 创建数据库连接引擎
engine = create_engine('mysql+pymysql://root:1234@localhost/db02')

# 读取数据，只处理 video_cluster_id 为 -1 的视频
data = pd.read_sql("SELECT video_id, video_title, video_tags FROM videos WHERE video_cluster_id = -1", engine)

# 检查数据是否为空
if data.empty:
    print("No data found with video_cluster_id = -1")
else:
    # 定义最新的大聚类标签和对应的ID
    cluster_mapping = {
        "综艺": 0, "动画": 1, "鬼畜": 2, "舞蹈": 3, "娱乐": 4, "科技数码": 5, "美食": 6, "汽车": 7,
        "体育运动": 8, "电影": 9, "电视剧": 10, "纪录片": 11, "游戏": 12, "音乐": 13, "影视": 14, "知识": 15,
        "资讯": 16, "小剧场": 17, "时尚美妆": 18, "动物": 19, "vlog": 20, "绘画": 21, "人工智能": 22,
        "家装房产": 23, "户外": 24, "健身": 25, "手工": 26, "旅游出行": 27, "三农": 28, "亲子": 29,
        "健康": 30, "情感": 31, "生活兴趣": 32, "生活经验": 33, "公益": 34
    }

    # 自动映射逻辑
    def classify_video(row):
        title = row['video_title']  # 获取视频标题
        tags = row['video_tags']   # 获取视频标签
        for cluster_name, cluster_id in cluster_mapping.items():
            # 检查标题或标签中是否包含聚类关键词
            if cluster_name in title or cluster_name in tags:
                return cluster_id  # 直接归类到对应的大聚类ID
        return -1  # 默认值，未匹配任何大聚类

    # 应用分类逻辑
    data['video_cluster_id'] = data.apply(classify_video, axis=1)

    # 更新数据库
    try:
        connection = engine.raw_connection()
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