import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main{
    public static void main(String[] args){
        Scanner sc=new Scanner(System.in);
        ArrayList<Course> courseSchedule=new ArrayList<Course>();
        boolean flag=true;
        while(flag){
            System.out.println("1 添加课程 2 查看所有课程 3 查找课程 4 退出");
            int n= 0;
            try {
                n = sc.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("输入错误，请重新输入");
                sc.next();
                continue;
            }
            switch(n){
                case 1:
                    System.out.println("请输入课程名称");
                    String lesson=sc.next();
                    System.out.println("请输入上课时间");
                    String space=sc.nextLine();
                    String time=sc.nextLine();
                    System.out.println("请输入教室");
                    String classroom=sc.next();
                    Course course=new Course(lesson,time,classroom);
                    courseSchedule.add(course);
                    break;
                case 2:
                    if(courseSchedule.isEmpty()){
                        System.out.println("暂无课程");
                        break;
                    }else{
                    for(int i=0;i<courseSchedule.size();i++){
                        Course c=courseSchedule.get(i);
                        System.out.println(c.lesson+"-"+c.time+"-"+c.classroom);
                    }
                    break;
                    }
                case 3:
                    boolean findFlag=false;
                    System.out.println("请输入课程名称");
                    String searchCourse=sc.next();
                    for(Course c:courseSchedule){
                            if(c.lesson.equals(searchCourse)){
                                System.out.println(c.lesson+"-"+c.time+"-"+c.classroom);
                                findFlag=true;
                                break;
                            }
                    }
                    if(!findFlag){
                        System.out.println("无此课程");
                    }
                    break;
                case 4:
                    System.out.println("再见！");
                    flag=false;
                    break;
            }
        }

    }

}

class Course{
    String lesson;
    String time;
    String classroom;
    public Course(String lesson, String time, String classroom){
        this.lesson=lesson;
        this.time=time;
        this.classroom=classroom;
    }
}