import os
import requests
from lxml import etree
from bs4 import BeautifulSoup
import re
import urllib.request

# 获取源码
def getHTML(url):
    a = urllib.request.urlopen(url)  # 打开指定网址
    html = a.read()  # 读取网页源码
    html = html.decode("utf-8")  # 解码为unicode码
    return str(html)

#去除HTML标签
def remoteHTMLTag(html):
    soup = BeautifulSoup(html, 'html.parser')
    return str(soup.get_text())

# 获取源码
def get_url(url):
    response = requests.get(url)
    response.encoding = 'utf-8'
    html = etree.HTML(response.text)
    return html

# 获取全部国内新闻
def allNews(url):
    html = get_url(url)
    name = html.xpath('//*[@id="rank-defList"]/div/div[2]/div/h3/a/text()')  # 新闻名称
    href = html.xpath('//*[@id="rank-defList"]/div/div[2]/div/h3/a/@href')  # 对应的新闻链接
    for num in range(len(name)):
        if(num<50):
            new(num,href[num],name[num])
        else:
            break

# 单个新闻内容(地址,新闻名称)
def new(index,url, header):
    # index 文件名称
    # print(getHTML(url)) #单个新闻源码
    # print(remoteHTMLTag(getHTML(url)))#单个新闻内容(去除HTML标签)
    # writeNewSrc(index,getHTML(url))
    writeNewTxt(index, header, remoteHTMLTag(getHTML(url)))
    print("完成"+header+"的爬取")
#打印src
def writeNewSrc(index, html):
    if not os.path.exists("src"):
        os.makedirs("src")
    filename = "src" + "//" + str(index+1) + ".txt"
    if not os.path.exists(filename):

        f = open(filename, 'a', encoding='utf-8')
        f.write(html)
        f.close()
        print("成功打印源碼,目录:"+filename)
#打印txt
def writeNewTxt(index,header,content):
    if not os.path.exists("txt"):
        os.makedirs("txt")
    filename = "txt" + "//" + str(index+1) + ".txt"
    removeContent=''.join(content.split("\n"))

    if not os.path.exists(filename):
        f = open(filename, 'a', encoding='utf-8')
        f.write(str(header))
        f.write("\n\n")
        f.write(removeContent)
        f.close()
        print("成功打印文件,目录:" + filename )

#
if __name__ == '__main__':
    url = 'https://news.china.com/domestic/index.html';
    allNews(url)
