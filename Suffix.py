
import os,sys

def change(path,source,target):
    count=0
    for root, dirs, files in os.walk(path):
        #root 路径
        #dir 路径下的所有目录(列表)
        #files 文件(列表)
        for each in range(len(files)):#遍历路径下的所有文件 files[each]
            if str(files[each]).endswith(source):
                name=files[each].replace(source,"")+target
                os.rename(path+"//"+files[each],path+"//"+name)
                count+=1
        print("总共完成修改了",count,"个文件")

if __name__ == '__main__':

    if len(sys.argv) < 3:
        print("使用：python change.py {dir} [原后缀] [目标后缀]")
        sys.exit(1)
    path=sys.argv[1]
    source=sys.argv[2]
    target=sys.argv[3]
    change(sys.argv[1],sys.argv[2],sys.argv[3])



