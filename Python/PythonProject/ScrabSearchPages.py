from bilibili_api import search
from bilibili_api import sync
import re
import pymysql
from pymysql.cursors import DictCursor
import time
import random

def clean_title(title):
    """清理标题中的HTML标签"""
    return re.sub(r'<[^>]+>', '', title)

def convert_duration(duration_str):
    """将播放时长转换为秒数"""
    parts = duration_str.split(":")
    seconds = 0
    try:
        if len(parts) == 2:
            seconds = int(parts[0]) * 60 + int(parts[1])
        elif len(parts) == 3:
            seconds = int(parts[0]) * 3600 + int(parts[1]) * 60 + int(parts[2])
    except Exception as e:
        print(f"Error converting duration '{duration_str}':", e)
    return seconds

def save_to_db(video_info):
    """将视频信息保存到数据库"""
    connection = pymysql.connect(
        host="localhost",
        user="root",
        password="1234",
        db="db02",
        charset="utf8mb4",
        cursorclass=DictCursor
    )
    try:
        with connection.cursor() as cursor:
            sql = """
            INSERT INTO videos (video_title, video_tags, video_url, video_views, video_duration, video_cover_image)
            VALUES (%s, %s, %s, %s, %s, %s)
            ON DUPLICATE KEY UPDATE 
                video_tags = VALUES(video_tags),
                video_views = VALUES(video_views),
                video_duration = VALUES(video_duration),
                video_cover_image = VALUES(video_cover_image)
            """
            cursor.execute(sql, (
                video_info["标题"],
                ",".join(video_info["视频标签"]),
                video_info["视频链接"],
                video_info["播放量"],
                video_info["播放时长"],
                video_info["封面图片"]
            ))
            print(f"SQL executed: {sql}")
            print(f"With parameters: {video_info}")
        connection.commit()
        print(f"Video saved: {video_info['标题']}")
    except Exception as e:
        print("Failed to save video:", e)
    finally:
        connection.close()

def search_bilibili_for_keywords(keywords, max_pages=25):
    """逐个关键词检索并保存到数据库"""
    for keyword in keywords:
        print(f"Searching for keyword: {keyword}")
        for page in range(1, max_pages + 1):
            # 发起搜索请求（无需登录）
            result = sync(search.search_by_type(
                keyword=keyword,
                search_type=search.SearchObjectType.VIDEO,
                page=page
            ))

            # 检查是否包含 "result" 键
            if "result" not in result:
                print(f"No result found for keyword '{keyword}' on page {page}")
                continue

            # 解析结果
            for item in result["result"]:
                # 转换播放时长为秒数
                duration_seconds = convert_duration(item["duration"])

                # 忽略播放时长超过30分钟的视频
                if duration_seconds > 1800:
                    continue

                # 提取核心字段
                video_info = {
                    "标题": clean_title(item["title"]),
                    "视频标签": item["tag"].split(",")[:4] if item.get("tag") else [],
                    "视频链接": f"https://www.bilibili.com/video/{item['bvid']}",
                    "播放量": item["play"],
                    "播放时长": duration_seconds,
                    "封面图片": item.get("pic")
                }
                print(video_info)

                save_to_db(video_info)

            # 在每次爬取页面之间添加随机延时（避免封IP）
            delay = random.uniform(0.75, 1.2)  # 随机延时 1 到 1.2 秒
            print(f"Pausing for {delay:.2f} seconds to avoid rate limiting...")
            time.sleep(delay)

if __name__ == "__main__":
    keywords = ["LPL","世界赛","奥运会","人类","就业","经济",
                "战争雷霆","学习"]
    search_bilibili_for_keywords(keywords)
