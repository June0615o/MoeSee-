import pandas as pd
from sqlalchemy import create_engine

# 创建数据库连接
engine = create_engine('mysql+pymysql://root:1234@localhost/db02')

# 读取用户表
users = pd.read_sql("SELECT user_id, user_preferred_tags, user_cluster_id FROM users", engine)

# 定义标签到大聚类 ID 的映射
tag_to_cluster = {
    "综艺": 0, "动画": 1, "鬼畜": 2, "舞蹈": 3, "娱乐": 4, "科技数码": 5, "美食": 6, "汽车": 7,
    "体育运动": 8, "电影": 9, "电视剧": 10, "纪录片": 11, "游戏": 12, "音乐": 13, "影视": 14, "知识": 15,
    "资讯": 16, "小剧场": 17, "时尚美妆": 18, "动物": 19, "vlog": 20, "绘画": 21, "人工智能": 22,
    "家装房产": 23, "户外": 24, "健身": 25, "手工": 26, "旅游出行": 27, "三农": 28, "亲子": 29,
    "健康": 30, "情感": 31, "生活兴趣": 32, "生活经验": 33, "公益": 34,
    "番剧": 1, "国创": 1
}

# 定义函数将用户偏好标签映射到大聚类 ID
def map_tags_to_cluster(preferred_tags):
    tags = preferred_tags.split(",")  # 假设标签用逗号分隔
    cluster_ids = [tag_to_cluster[tag.strip()] for tag in tags if tag.strip() in tag_to_cluster]
    return ",".join(map(str, cluster_ids))  # 用逗号分隔多个聚类 ID

# 应用映射函数，更新用户的聚类 ID
users['user_cluster_id'] = users['user_preferred_tags'].apply(map_tags_to_cluster)

# 更新数据库中的 `user_cluster_id` 列
try:
    connection = engine.connect()
    for index, row in users.iterrows():
        sql = """
        UPDATE users
        SET user_cluster_id = %s
        WHERE user_id = %s
        """
        # 将参数封装为元组
        connection.execute(sql, (row['user_cluster_id'], row['user_id']))
    print("User table updated successfully.")
except Exception as e:
    print("Failed to update user table:", e)
finally:
    connection.close()

