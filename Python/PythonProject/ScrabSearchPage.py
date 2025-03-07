import requests
import pymysql
import time
from pymysql.err import IntegrityError

class BZhan:
    def __init__(self):
        self.headers = {
            "Origin": "https://www.bilibili.com",
            "Pragma": "no-cache",
            "Referer": "https://www.bilibili.com/v/popular/all/?spm_id_from=333.1007.0.0",
            "Sec-Fetch-Dest": "empty",
            "Sec-Fetch-Mode": "cors",
            "Sec-Fetch-Site": "same-site",
            "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0"
        }
        self.url = "https://api.bilibili.com/x/web-interface/popular"
        # 数据库连接配置，修改为你的实际信息
        self.db_config = {
            "host": "localhost",
            "user": "your_username",
            "password": "your_password",
            "db": "db02",
            "charset": "utf8mb4"
        }

    def get_data(self, page):
        params = {
            "ps": "20",
            "pn": str(page),
        }
        response = requests.get(self.url, headers=self.headers, params=params)
        # 假设返回的数据结构能够从 JSON 中获得视频列表，注意：此处返回的是 data 下面的 list
        res = response.json()['data']['list']
        return res

    def parse_data(self, res):
        for data in res:
            item = {
                '标题': data['title'],
                '视频分类': data['tname'],  # 这里取的是第一个标签，如果需要多个，可结合 tnamev2
                '播放量': data['stat']['view'],
                '播放时长': data['duration'],
                'url': data['short_link_v2'],
            }
            print(item)
            self.save_video_to_db(item)
            # 防止请求速度过快（可以根据需要调整延时）
            time.sleep(0.1)

    def save_video_to_db(self, item):
        connection = None
        try:
            connection = pymysql.connect(**self.db_config)
            with connection.cursor() as cursor:
                sql = """
                INSERT INTO videos (video_title, video_tags, video_url, video_views, video_duration)
                VALUES (%s, %s, %s, %s, %s)
                """
                cursor.execute(sql, (item['标题'], item['视频分类'], item['url'], item['播放量'], item['播放时长']))
            connection.commit()
            print("Video saved:", item['标题'])
        except IntegrityError as e:
            print("Duplicate entry for video:", item['标题'])
        except Exception as e:
            print("Failed to save video:", item['标题'], e)
        finally:
            if connection:
                connection.close()

    def main(self):
        # 例如只爬取前 9 页用于测试
        for i in range(1, 2):
            print("Fetching page", i)
            res = self.get_data(i)
            self.parse_data(res)

if __name__ == '__main__':
    bz = BZhan()
    bz.main()