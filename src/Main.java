import java.util.InputMismatchException;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
public class Main{
    public static void main(String[] args) throws SQLException{
        Connection conn=null;
        Scanner sc=new Scanner(System.in);
        boolean flag=true;
        while(flag){
            System.out.println("1 添加课程/2 查看所有课程（join连表查询）/3 查找课程/4 退出/5 调课/6 按星期查询");
            int n= 0;
            try{
                n = sc.nextInt();
            }catch(InputMismatchException e){
                System.out.println("输入错误，请重新输入");
                sc.next();
                continue;
            }
            switch(n){
                case 1:
                    System.out.println("请输入课程名称");
                    sc.nextLine();
                    String lesson=sc.nextLine();
                    System.out.println("请输入上课时间");
                    String time=sc.nextLine();
                    System.out.println("请输入教室");
                    String classroom=sc.nextLine();
                    try{
                        conn = JDBCUtil.getConn();
                        int timeId;
                        String sqlSelectTimeId="select id from course_time where time_name = ?";
                        PreparedStatement pstmtTime = conn.prepareStatement(sqlSelectTimeId);
                        pstmtTime.setString(1, time);
                        ResultSet rsTimeId = pstmtTime.executeQuery();
                        if(rsTimeId.next()){
                            timeId = rsTimeId.getInt("id");
                        }else{
                            String sqlInsertTime="insert into course_time(time_name) values(?)";
                            PreparedStatement pstmtInsertTime = conn.prepareStatement(sqlInsertTime, PreparedStatement.RETURN_GENERATED_KEYS);
                            pstmtInsertTime.setString(1, time);
                            pstmtInsertTime.executeUpdate();
                            ResultSet rsTimeKeys= pstmtInsertTime.getGeneratedKeys();
                            rsTimeKeys.next();
                            timeId = rsTimeKeys.getInt(1);
                            rsTimeKeys.close();
                            pstmtInsertTime.close();
                        }
                        rsTimeId.close();
                        pstmtTime.close();
                        int roomId;
                        String sqlSelectRoom="select id from classroom where classroom_name = ?";
                        PreparedStatement pstmtRoom=conn.prepareStatement(sqlSelectRoom);
                        pstmtRoom.setString(1, classroom);
                        ResultSet rsRoomId=pstmtRoom.executeQuery();
                        if(rsRoomId.next()){
                            roomId = rsRoomId.getInt("id");
                        }else{
                            String sqlInsertRoom="insert into classroom(classroom_name) values(?)";
                            PreparedStatement pstmtInsertRoom=conn.prepareStatement(sqlInsertRoom,PreparedStatement.RETURN_GENERATED_KEYS);
                            pstmtInsertRoom.setString(1, classroom);
                            pstmtInsertRoom.executeUpdate();
                            ResultSet rsRoomKey=pstmtInsertRoom.getGeneratedKeys();
                            rsRoomKey.next();
                            roomId = rsRoomKey.getInt(1);
                            rsRoomKey.close();
                            pstmtInsertRoom.close();
                        }
                        rsRoomId.close();
                        pstmtRoom.close();
                        String sqlCourse="insert into course(course_name, time_id, classroom_id) values(?,?,?)";
                        PreparedStatement pstmtCourse=conn.prepareStatement(sqlCourse);
                        pstmtCourse.setString(1, lesson);
                        pstmtCourse.setInt(2, timeId);
                        pstmtCourse.setInt(3, roomId);
                        int row = pstmtCourse.executeUpdate();
                        pstmtCourse.close();
                    }catch (Exception e){
                        throw new RuntimeException(e);
                    }finally{
                        try{
                            if(conn != null){
                                conn.close();
                            }
                        }catch(Exception e){
                            throw new RuntimeException(e);
                        }
                    }
                    System.out.println("课程成功添加");
                    break;
                case 2:
                    String sqlSelectCourse2="select c.course_name, ct.time_name, cr.classroom_name from course c join course_time ct on c.time_id = ct.id join classroom cr on c.classroom_id = cr.id";
                    try(
                        Connection conn2=JDBCUtil.getConn();
                        PreparedStatement pstmt2=conn2.prepareStatement(sqlSelectCourse2);
                        ResultSet rs2 = pstmt2.executeQuery();
                    ){
                    boolean hasCourseFlag = false;
                    while(rs2.next()){
                    hasCourseFlag = true;
                    String lessonName = rs2.getString("course_name");
                    String timeName = rs2.getString("time_name");
                    String roomName = rs2.getString("classroom_name");
                    System.out.println(lessonName + "-" + timeName + "-" + roomName);
                    }
                    if (!hasCourseFlag) {
                        System.out.println("暂无课程");
                    }
                }
                    break;
                case 3:
                    boolean findFlag=false;
                    System.out.println("请输入课程名称");
                    sc.nextLine();
                    String searchCourse=sc.nextLine();
                    conn=JDBCUtil.getConn();
                    String sqlSelectCourse3="select c.course_name, ct.time_name, cr.classroom_name from course c join course_time ct on c.time_id = ct.id join classroom cr on c.classroom_id = cr.id where c.course_name=?";
                    PreparedStatement pstmt3=conn.prepareStatement(sqlSelectCourse3);
                    pstmt3.setString(1,searchCourse);
                    ResultSet rs3=pstmt3.executeQuery();
                    while(rs3.next()){
                        findFlag=true;
                        String lessonName = rs3.getString("course_name");
                        String timeName = rs3.getString("time_name");
                        String roomName = rs3.getString("classroom_name");
                        System.out.println(lessonName + "-" + timeName + "-" + roomName);
                    }
                    if(!findFlag){
                        System.out.println("无此课程");
                    }
                    rs3.close();
                    pstmt3.close();
                    conn.close();
                    break;
                case 4:
                    System.out.println("再见！");
                    flag=false;
                    break;
                case 5:
                    int timeId=0;
                    System.out.println("请输入课程名称");
                    sc.nextLine();
                    String courseName=sc.nextLine();
                    System.out.println("请输入调课时间");
                    String newTime=sc.nextLine();
                    conn=JDBCUtil.getConn();
                    PreparedStatement pstmt5=null;
                    ResultSet rs5=null;
                    try {
                        if(conn==null){
                            System.out.println("数据库连接失败");
                            return;
                        }
                        conn.setAutoCommit(false);
                        String sqlSelectTimeId5="select id from course_time where time_name=?";
                        pstmt5=conn.prepareStatement(sqlSelectTimeId5);
                        pstmt5.setString(1,newTime);
                        rs5=pstmt5.executeQuery();
                        if(rs5.next()){
                            timeId=rs5.getInt("id");
                        }else{
                            String sqlInsertTime="Insert into course_time(time_name) values(?)";
                            PreparedStatement pstmtInsertTime=conn.prepareStatement(sqlInsertTime, PreparedStatement.RETURN_GENERATED_KEYS);
                            pstmtInsertTime.setString(1, newTime);
                            pstmtInsertTime.executeUpdate();
                            ResultSet rsTimeId= pstmtInsertTime.getGeneratedKeys();
                            rsTimeId.next();
                            timeId=rsTimeId.getInt(1);
                            rsTimeId.close();
                            pstmtInsertTime.close();
                        }
                        String sqlUpdateTime="update course set time_id=? where course_name=?";
                        PreparedStatement pstmtUpdateTime=conn.prepareStatement(sqlUpdateTime);
                        pstmtUpdateTime.setInt(1, timeId);
                        pstmtUpdateTime.setString(2, courseName);
                        pstmtUpdateTime.executeUpdate();
                        pstmtUpdateTime.close();
                        conn.commit();
                        System.out.println("调课成功");
                    }catch(Exception e){
                        try{
                            if(conn!=null){
                            conn.rollback();
                        }
                        }catch(Exception e1){
                            e1.printStackTrace();
                        }
                        e.printStackTrace();
                    }finally{
                        if(rs5!=null){
                            rs5.close();
                        }
                        if(pstmt5!=null){
                            pstmt5.close();
                        }
                        if(conn!=null){
                            conn.setAutoCommit(true);
                            conn.close();
                        }
                    }
                    break;
                case 6:{
                    PreparedStatement pstmt=null;
                    ResultSet rs=null;
                    try{
                        conn=JDBCUtil.getConn();
                        String sql="select substring_index(ct.time_name,' ',1), count(*) from course c join course_time ct on c.time_id = ct.id group by substring_index(ct.time_name,' ',1)";
                        pstmt=conn.prepareStatement(sql);
                        rs=pstmt.executeQuery();
                        while(rs.next()) {
                            String week=rs.getString(1);
                            int count=rs.getInt(2);
                            System.out.println(week+":"+count+"节课");
                        }
                    }catch(Exception e) {
                        e.printStackTrace();
                    }finally{
                        if(rs!=null){
                            rs.close();
                        }
                        if(pstmt!=null){
                            pstmt.close();
                        }
                        if(conn!=null){
                            conn.close();
                        }
                    }
                }
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