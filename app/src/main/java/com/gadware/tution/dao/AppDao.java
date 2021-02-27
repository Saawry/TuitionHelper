package com.gadware.tution.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.gadware.tution.models.Batch;
import com.gadware.tution.models.DaySchedule;
import com.gadware.tution.models.ImageDetails;
import com.gadware.tution.models.SessionInfo;
import com.gadware.tution.models.StudentInfo;
import com.gadware.tution.models.StudentList;
import com.gadware.tution.models.TuitionInfo;
import com.gadware.tution.models.User;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface AppDao {

    //User
    @Query("insert or replace into UserInfo (uid,name,email,address,mobile,status) VALUES(:uid,:name,:email,:address,:mobile,:status)")
    void insertUserInfoDetails(String uid,String name,String email, String address,String mobile, String status );

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUserInfoList(List<User> userList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUserInfo(User user);

    @Query("update UserInfo set name=:name where uid=:id")
    void updateUserName(String name,String id );

    @Query("update UserInfo set name=:name where uid=:id")
    void updateUserMobile(String name,String id );

    @Query("update UserInfo set name=:name where uid=:id")
    void updateUserAddress(String name,String id);

    @Query("Select * from UserInfo")
    Flowable<List<User>> getUserList();

    @Query("Select  * from UserInfo where uid=:id")
    Single<User> getUserInfo(String id);

    @Query("delete from UserInfo where uid=:id")
    void deleteUserInfo(String id);

    @Query("delete from UserInfo")
    void deleteUserInfoTable();




    //TuitionInfo
    @Query("insert or replace into TuitionInfo (id,studentName,location,mobile,totalDays,completedDays,weeklyDays,remuneration,sDate,eDate) VALUES(:id,:studentName,:location,:mobile,:totalDays,:completedDays,:weeklyDays,:remuneration,:sDate,:eDate)")
    void insertTuitionInfoDetails(String id, String studentName, String location, String mobile, String totalDays, String completedDays, String weeklyDays, String remuneration, String sDate, String eDate);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTuitionInfoList(List<TuitionInfo> tuitionInfoList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTuitionInfo(TuitionInfo tuitionInfo);

    @Query("update TuitionInfo set studentName=:studentName where id=:id")
    void updateTSName(String studentName,String id );

    @Query("update TuitionInfo set mobile=:mobile where id=:id")
    void updateTSMobile(String mobile,String id );

    @Query("update TuitionInfo set location=:value where id=:id")
    void updateTSAddress(String value,String id);

    @Query("update TuitionInfo set totalDays=:value where id=:id")
    void updateTTotalDays(String value,String id);

    @Query("update TuitionInfo set completedDays=:value where id=:id")
    void updateTCompDays(String value,String id);

    @Query("update TuitionInfo set weeklyDays=:value where id=:id")
    void updateTWkDays(String value,String id);

    @Query("update TuitionInfo set remuneration=:value where id=:id")
    void updateTRemu(String value,String id);

    @Query("update TuitionInfo set sDate=:value where id=:id")
    void updateTsDT(String value,String id);

    @Query("update TuitionInfo set eDate=:value where id=:id")
    void updateTeDT(String value,String id);

    @Query("Select * from TuitionInfo")
    Flowable<List<TuitionInfo>> getTuitionInfoList();

    @Query("Select  * from TuitionInfo where id=:id")
    Single<TuitionInfo> getTuitionInfo(String id);

    @Query("delete from TuitionInfo where id=:id")
    void deleteTuitionInfo(String id);

    @Query("delete from TuitionInfo")
    void deleteTuitionInfoTable();




    //StudentInfo
    @Query("insert or replace into StudentInfo (id,name,className,institute,fatherName,address,phone,email,reference) VALUES(:id,:name,:className,:institute,:fatherName,:address,:phone,:email,:ref)")
    void insertStudentInfoDetails(String id, String name, String className, String institute, String fatherName, String address, String phone,String email,String ref);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertStudentInfo(StudentInfo studentInfo);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertStudentInfoList(List<StudentInfo> studentInfoList);

    @Query("update StudentInfo set name=:value where id=:id")
    void updateStdName(String value,String id );

    @Query("update StudentInfo set className=:value where id=:id")
    void updateStdClassName(String value,String id );

    @Query("update StudentInfo set institute=:value where id=:id")
    void updateStdInstitute(String value,String id );

    @Query("update StudentInfo set fatherName=:value where id=:id")
    void updateStdFatherName(String value,String id );

    @Query("update StudentInfo set address=:value where id=:id")
    void updateStdAddress(String value,String id );

    @Query("update StudentInfo set phone=:value where id=:id")
    void updateStdPhone(String value,String id );

    @Query("update StudentInfo set email=:value where id=:id")
    void updateStdEmail(String value,String id );

    @Query("Select * from StudentInfo")
    Flowable<List<StudentInfo>> getStudentInfoList();

    @Query("Select  * from StudentInfo where id=:id")
    Single<StudentInfo> getStudentInfo(String id);

    @Query("delete from StudentInfo where id=:id")
    void deleteStudentInfo(String id);

    @Query("delete from StudentInfo")
    void deleteStudentInfoTable();




    //SessionInfo
    @Query("insert or replace into SessionInfo (id,btId,type,date,day,sTime,eTime,topic,tutorId,counter) VALUES(:id,:btId,:type,:date,:day,:sTime,:eTime,:topic,:tutorId,:counter)")
    void insertSessionInfoDetails(String id,String btId,String type, String date, String day, String sTime, String eTime, String topic,String tutorId, String counter);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSessionInfo(SessionInfo sessionInfo);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSessionInfoList(List<SessionInfo> sessionInfoList);

    @Query("update SessionInfo set date=:value where id=:id")
    void updateSsnDate(String value,String id );

    @Query("update SessionInfo set day=:value where id=:id")
    void updateSsnDay(String value,String id );

    @Query("update SessionInfo set sTime=:value where id=:id")
    void updateSsnsTime(String value,String id);

    @Query("update SessionInfo set eTime=:value where id=:id")
    void updateSsneTime(String value,String id);

    @Query("update SessionInfo set topic=:value where id=:id")
    void updateSsnTopic(String value,String id);

    @Query("update SessionInfo set counter=:value where id=:id")
    void updateSsnCounter(String value,String id);

    @Query("Select * from SessionInfo")
    Flowable<List<SessionInfo>> getSessionInfoList();

    @Query("Select  * from SessionInfo where id=:id")
    Single<SessionInfo> getSessionInfo(String id);

    @Query("delete from SessionInfo where id=:id")
    void deleteSessionInfo(String id);

    @Query("delete from SessionInfo")
    void deleteSessionInfoTable();





    //DaySchedule
    @Query("insert or replace into DaySchedule (bTId, dayName,aTime, time) VALUES(:bTId,:dayName,:aTime,:time)")
    void insertDaySchedule(String bTId,String dayName, String aTime,String time);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDayScheduleList(List<DaySchedule> dayScheduleList);

    @Query("Select * from DaySchedule")
    Flowable<List<DaySchedule>> getDayScheduleList();

    @Query("Select * from DaySchedule where dayName=:dayName")
    Flowable<List<DaySchedule>> getDayScheduleListToday(String dayName);

    @Delete
    void deleteDaySchedule(DaySchedule daySchedule);

    @Query("delete from DaySchedule")
    void deleteDayScheduleTable();





    //Batch
    @Query("insert or replace into Batch (id,className,subject,studentsAmount,remuneration,weeklyDays) VALUES(:id,:className,:subject,:studentsAmount,:remuneration,:weeklyDays)")
    void insertBatchInfoDetails(String id, String className, String subject, String studentsAmount, String remuneration, String weeklyDays);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBatchInfoList(List<Batch> batchList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBatchInfo(Batch batch);

    @Query("update Batch set className=:value where id=:id")
    void updateBatchClass(String value,String id );

    @Query("update Batch set className=:value where id=:id")
    void updateBatchSubject(String value,String id);

    @Query("update Batch set studentsAmount=:value where id=:id")
    void updateBatchStdCounter(String value,String id);

    @Query("update Batch set remuneration=:value where id=:id")
    void updateBatchRemu(String value,String id);

    @Query("update Batch set weeklyDays=:value where id=:id")
    void updateBatchWD(String value,String id);

    @Query("Select * from Batch")
    Flowable<List<Batch>> getBatchInfoList();

    @Query("Select  * from Batch where id=:id")
    Single<Batch> getBatchInfo(String id);

    @Query("delete from Batch where id=:id")
    void deleteBatchInfo(String id);

    @Query("delete from Batch")
    void deleteBatchInfoTable();




    //StudentList
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertStudentList(List<StudentList> studentLists);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSingleStudent(StudentList studentList);

    @Query("Select * from StudentList")
    Flowable<List<StudentList>> getAllStudentIdList();

    @Query("Select  * from StudentList where batchId=:batchId")
    Flowable<List<StudentList>> getBatchStudentList(String batchId);

    @Query("delete from StudentList where batchId=:batchId")
    void deleteBatchAllStudent(String batchId);

    @Query("delete from StudentList where studentId=:studentId")
    void deleteStudentFromBatch(String studentId);

    @Query("delete from Batch")
    void deleteStudentListTable();


    //ImageDetails
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertImageList(List<ImageDetails> imageDetailsList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSingleImage(ImageDetails imageDetails);

    @Query("Select * from ImageDetails")
    Flowable<List<ImageDetails>> getAllImageDetails();

    @Query("Select * from ImageDetails where type=:type")
    Flowable<List<ImageDetails>> getBTTypeImages(String type);

    @Query("Select  * from ImageDetails where id=:id and type=:type")
    Single<ImageDetails> getSingleImage(String id,String type);

    @Query("delete from ImageDetails where id=:id and type=:type")
    void deleteSingleImage(String id,String type);

    @Query("delete from ImageDetails")
    void deleteImageDetailsTable();

}
