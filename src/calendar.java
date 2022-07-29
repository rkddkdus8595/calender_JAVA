
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import javax.swing.*;

public class calendar extends JFrame implements ActionListener{
    JButton[] btn = new JButton[49]; // 일 버튼 넉넉잡아 만들기
    Calendar cal=new GregorianCalendar(Locale.KOREA);
    String week[] = { "SUN", "MON", "TUE", "WED", "THR", "FRI", "SAT" };
    int caldays[]={31,28,31,30,31,30,31,31,30,31,30,31}; // 각 월마다 일 수
    public int year, month, days,secdays, today;
    int check;
    public String memos="";
    
    Calendar todays = Calendar.getInstance();
    String strDriver="com.microsoft.sqlserver.jdbc.SQLServerDriver";
    String strURL="jdbc:sqlserver://localhost:1433;DatabaseName=CalendarMemo";
    String strUser="sa";
    String strPWD="dlsgkvmfwpr@_";
    String sql=new String();
    Connection DB_con;
    Statement DB_stmt;
    ResultSet DB_rs;
    
    String id="";
    
    public calendar() {
        initComponents();
        id=new MainFrame().getID(); // 아이디 값 받아오기
        pan.setBackground(new Color(152,197,147));
        pan.setLayout(new GridLayout(7,7));
        getContentPane().setBackground(new Color(242,255,237)); // 프레임 배경 화이트로 
        this.setTitle("캘린더"); // 타이틀은 캘린더
        todaysetting(); // 오늘날짜 셋팅
        make(); // 버튼 칸 미리 만들어주고
        calsetting(); // 달력 셋팅하고
        hide(); // 숫자 없는 곳은 비활성화 시키기
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(Integer.parseInt(e.getActionCommand()) >= 1 && 
                Integer.parseInt(e.getActionCommand()) <=31){
                    secdays = Integer.parseInt(e.getActionCommand());
                    SecondFrame sc=new SecondFrame(); // 값넘겨주기
                    sc.curYear=year;
                    sc.curMonth=month;
                    sc.curDays=secdays;
                    memoOpen(); //sql문 실행메소드
                    sc.txtMemo.setText(memos); // 불러온 메모를 sc의 텍스트에 셋팅
                    calsetting();
                    sc.setVisible(true);
        }
    }
    public void memoCheck(){ // 메모가 있는 날짜 체크하기
       sql = "select * from calendar where year='";
                     sql += year +"' and month='";
                     sql += month +"' and day='";
                     sql += today +"';";
        try{
            dbOpen();
            DB_rs=DB_stmt.executeQuery(sql);
            if(DB_rs.next()){
                   check=1;
            }
            else check=0;
            DB_stmt.close();
            DB_con.close();
              }
        catch(Exception e)
        {
               e.getMessage();
        }
                     
    }
    public void memoOpen(){ // 메모 열 때 메모 불러오기
        try{
                    dbOpen();
                     sql = "select Memo from calendar where year='";
                     sql += year +"' and month='";
                     sql += month +"' and day='";
                     sql += secdays +"' and id='";
                     sql += id+"';"; // 본인 아이디에 맞는 것만 불러옴 없으면 깨끗한 메모창만 !
                     System.out.println(sql);
                     DB_rs=DB_stmt.executeQuery(sql);
                     String txt="";

                    while(DB_rs.next()){
                           txt+=DB_rs.getString("memo")+" ";
                     }
                     memos=txt;
                     System.out.println(memos);
                     DB_stmt.close();
                     DB_con.close();
 
             }catch(Exception e){
                    e.getMessage();
             }
    }
    
    public void dbOpen(){
        try{
            Class.forName(strDriver);
            DB_con=DriverManager.getConnection(strURL,strUser,strPWD);
            DB_stmt=DB_con.createStatement();
        }catch(Exception e){
            System.out.println("SQLException: "+e.getMessage());
        }
        
    }
    public void dbClose(){
        try{
            DB_con.close();
            DB_stmt.close();
        }catch(Exception e){
            System.out.println("SQLException: "+e.getMessage());
        }
    }
    public void todaysetting(){ // 오늘 날짜 셋팅
        year=todays.get(Calendar.YEAR);
        month=todays.get(Calendar.MONTH)+1; // 달은 0월부터 시작해서 +1 해주기
        days=todays.get(Calendar.DAY_OF_MONTH);
        lblToday.setText(year+" / "+month+" / "+days);
        lblMonth.setText(month+"");
        lblYear.setText(year+"");
    }
    public void make(){ // 얘는 버튼 자체의 공간을 만드는 것
         for(int i = 0 ; i < week.length;i++){
               pan.add(btn[i] = new JButton(week[i]));  
               btn[i].setBackground(Color.WHITE);
               btn[i].setFont(new Font("맑은고딕",Font.BOLD,13));
         }
         for(int i = week.length ; i < 49;i++){    // 요일을 붙이고 난 뒤 숫자부터            
                    pan.add(btn[i] = new JButton("")); 
                    btn[i].setBackground(Color.WHITE);
                    btn[i].setFont(new Font("맑은고딕",Font.PLAIN,13));
             }   
         
       }
         
    public void calsetting(){ // 캘린더에 숫자 넣어주기
        cal.set(Calendar.YEAR,year);
        cal.set(Calendar.MONTH,month-1); // 달은 0월 부터 시작해서 month에 +1 해줬었으니까
        // 실제비교는 -1을 해서 비교해줘야한다.
        cal.set(Calendar.DATE,1); // 현재 월의 날짜의 첫번째 날짜로 셋팅
        int weeks = cal.get(Calendar.DAY_OF_WEEK); // 현재 월의 첫째날로 되어있기때문에
        // 그 달의 첫번째 날짜의 요일이 반환됨
        // 위에거 현재 요일 (일요일은 1, 토요일은 7)
        int cnt=0;
        btn[0].setForeground(Color.RED);
        btn[6].setForeground(Color.BLUE);
        for(int i=cal.getFirstDayOfWeek();i<weeks;i++){
            cnt++;  // 우리나라 시작 요일이 어떤 요일인지 알아내기 ~달의 시작 요일전까지 반복문
        }
        for(int j=0; j<cnt;j++){
            btn[j+7].setText(""); // 당 월의 시작 날짜 전까지는 빈 버튼으로 배치할 것
        }
        
        // 밑에는 현재 월의 날짜에서 최솟값을 가져온 것 
        for(int i=cal.getActualMinimum(Calendar.DAY_OF_MONTH);i<=cal.getActualMaximum(Calendar.DAY_OF_MONTH);i++){
            cal.set(Calendar.DATE,i);
               if(cal.get(Calendar.MONTH) !=month-1){// 달은 0월 부터 시작해서 month에 +1 해줬었으니까
                    // 실제비교는 -1을 해서 비교해줘야한다.
                    JOptionPane.showMessageDialog(null, "달이 맞지않습니다.");
                    break;
               }
               today=i;
                dbOpen();
                memoCheck();
                if(check==1){	// 메모가 있을 때
                    btn[6+cnt+i].setForeground(Color.magenta);
                }
                else{
                      btn[6+cnt+i].setForeground(Color.BLACK); // 날짜+요일쓴거6+빈공간
                      if(cal.get(Calendar.DAY_OF_WEEK)==1){//일요일 날짜 일때 색 입히기 1 일 2 월 3 화 4 수 5 목 6 금 7 토ㅋㅋ
                            btn[6+cnt+i].setForeground(Color.RED);
                           }
                      if(cal.get(Calendar.DAY_OF_WEEK)==7){//토요일 날짜 일때 색 입히기
                            btn[6+cnt+i].setForeground(Color.BLUE);
                           }
                    }
                    btn[6+cnt+i].setText((i)+"");
                    btn[6+cnt+i].addActionListener(this); // 버튼 마다 액션리스너 넣어주기
             }
    }
    public void hide(){
             for(int i = 0 ; i < btn.length;i++){
                    if((btn[i].getText()).equals(""))
                           btn[i].setEnabled(false);
             }
       }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnToday = new javax.swing.JButton();
        btnSub2 = new javax.swing.JButton();
        btnSub = new javax.swing.JButton();
        lblMonth = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblYear = new javax.swing.JLabel();
        btnAdd = new javax.swing.JButton();
        btnAdd2 = new javax.swing.JButton();
        lblToday = new javax.swing.JLabel();
        pan = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        lblColor = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnToday.setText("Today");
        btnToday.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTodayActionPerformed(evt);
            }
        });

        btnSub2.setText("<<");
        btnSub2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSub2ActionPerformed(evt);
            }
        });

        btnSub.setText("<");
        btnSub.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSubActionPerformed(evt);
            }
        });

        lblMonth.setFont(new java.awt.Font("굴림", 1, 12)); // NOI18N
        lblMonth.setText("월");

        jLabel2.setFont(new java.awt.Font("굴림", 1, 18)); // NOI18N
        jLabel2.setText("/");

        lblYear.setFont(new java.awt.Font("굴림", 1, 12)); // NOI18N
        lblYear.setText("년");

        btnAdd.setText(">");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnAdd2.setText(">>");
        btnAdd2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdd2ActionPerformed(evt);
            }
        });

        lblToday.setText("오늘 날짜");

        pan.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout panLayout = new javax.swing.GroupLayout(pan);
        pan.setLayout(panLayout);
        panLayout.setHorizontalGroup(
            panLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        panLayout.setVerticalGroup(
            panLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 307, Short.MAX_VALUE)
        );

        jLabel1.setText("메모가 있는 날짜 : ");

        lblColor.setForeground(new java.awt.Color(255, 0, 255));
        lblColor.setText("보라색");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnToday)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblToday))
                    .addComponent(pan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnSub2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnSub)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblColor))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblMonth)
                        .addGap(14, 14, 14)
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(lblYear)
                        .addGap(27, 27, 27)
                        .addComponent(btnAdd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAdd2)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnToday)
                    .addComponent(lblToday)
                    .addComponent(jLabel1)
                    .addComponent(lblColor))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSub2)
                    .addComponent(btnSub)
                    .addComponent(lblMonth)
                    .addComponent(jLabel2)
                    .addComponent(lblYear)
                    .addComponent(btnAdd)
                    .addComponent(btnAdd2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(43, 43, 43))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
         month=month+1;
        if(month>=13){
            month=1;
        }
        lblMonth.setText(month+"");
        pan.removeAll();
        make();
        calsetting();
        hide();
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnSubActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubActionPerformed
        month=month-1;
        if(month<=0){
            month=12;
        }
        lblMonth.setText(month+"");
        pan.removeAll();
        make();
        calsetting();
        hide();
    }//GEN-LAST:event_btnSubActionPerformed

    private void btnAdd2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdd2ActionPerformed
        year=year+1;
        lblYear.setText(year+"");
        pan.removeAll();
        make();
        calsetting();
        hide();
    }//GEN-LAST:event_btnAdd2ActionPerformed

    private void btnSub2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSub2ActionPerformed
        year=year-1;
        lblYear.setText(year+"");
        pan.removeAll();
        make();
        calsetting();
        hide();
    }//GEN-LAST:event_btnSub2ActionPerformed

    private void btnTodayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTodayActionPerformed
        year=todays.get(Calendar.YEAR);
        month=todays.get(Calendar.MONTH)+1;
        days=todays.get(Calendar.DAY_OF_MONTH);
        pan.removeAll();
        lblYear.setText(year+"");
        lblMonth.setText(month+"");
        make();
        calsetting();
        hide();
    }//GEN-LAST:event_btnTodayActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(calendar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(calendar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(calendar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(calendar.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new calendar().setVisible(true);
                             }
                         });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnAdd2;
    private javax.swing.JButton btnSub;
    private javax.swing.JButton btnSub2;
    private javax.swing.JButton btnToday;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel lblColor;
    private javax.swing.JLabel lblMonth;
    private javax.swing.JLabel lblToday;
    private javax.swing.JLabel lblYear;
    private javax.swing.JPanel pan;
    // End of variables declaration//GEN-END:variables

}
