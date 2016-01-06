//*************
// Nat Lee
//*************

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.filechooser.FileFilter;
import java.io.*;
import java.awt.*;
import java.awt.event.*; //引用處理事件的event套件
public class MDIEditor extends JFrame {

    JDesktopPane dpPane = new JDesktopPane(); //容納內部框架的虛擬桌面

    WindowMenu wmWindow = new WindowMenu("視窗(W)", KeyEvent.VK_W);
    //控制內部視窗畫面切換的功能表

    JMenuItem miCut, miCopy, miPaste; //執行編輯動作的功能表選項
    JCheckBoxMenuItem cbmiSize16, cbmiSize18, cbmiSize20;//控制字級大小的核取方塊選項
    JToggleButton tbnSize16, tbnSize18, tbnSize20;//控制字級大小的工具列按鈕
    JToolBar tbFontSize;
    JLabel lbStatus; //顯示游標位置與選取字元的標籤


    AudioPlayer openYee = new AudioPlayer();//轉檔音效控制項
    java.net.URL Yee = MDIEditor.class.getResource("/voice/Yee.aiff");//取得Yee.aiff的URL

    PlayMP3 player = null;

    boolean loop = true;//無窮迴圈
    
    Editor fontSize;
    Action acCut, acCopy, acPaste; //執行編輯動作的Action物件
    
    MDIEditor(String title) {
        super(title);//設定視窗名稱
        JMenu mnFontSize = new JMenu("字級(S)"); //宣告字級功能表
        mnFontSize.setMnemonic(KeyEvent.VK_S); //設定字級功能表的記憶鍵
        fontSize = new Editor(mnFontSize,MDIEditor.this,wmWindow);
        fontSize.createInternalFrame(); //建立第一個內部框架

        JMenu mnFile = new JMenu("檔案(F)"); //宣告檔案功能表
        mnFile.setMnemonic(KeyEvent.VK_F); //設定檔案功能表使用的記憶鍵

        JMenuItem miNew = new JMenuItem("新增(N)", KeyEvent.VK_N),
                miOpen = new JMenuItem("開啟舊檔(O)", KeyEvent.VK_O),
                miSave = new JMenuItem("儲存檔案(S)", KeyEvent.VK_S),
                miSaveAn = new JMenuItem("另存新檔(A)", KeyEvent.VK_A),
                miYee = new JMenuItem("PDF轉檔(Y)", KeyEvent.VK_Y),
                miExit = new JMenuItem("結束(E)", KeyEvent.VK_E);
        //宣告檔案功能表的選項

        miNew.addActionListener(alFile); //為功能表選項加上監聽器
        miOpen.addActionListener(alFile);
        miSave.addActionListener(alFile);
        miSaveAn.addActionListener(alFile);
        miYee.addActionListener(alFile);
        miExit.addActionListener(alFile);

        mnFile.add(miNew); //將選項加入檔案功能表
        mnFile.add(miOpen);
        mnFile.add(miSave);
        mnFile.add(miSaveAn);
        mnFile.add(miYee);
        mnFile.addSeparator();
        mnFile.add(miExit);

        JMenu mnEdit = new JMenu("編輯(E)"); //宣告編輯功能表
        mnEdit.setMnemonic(KeyEvent.VK_E); //設定編輯功能表的記憶鍵

        acCut = getActionByName(DefaultEditorKit.cutAction);
        acCopy = getActionByName(DefaultEditorKit.copyAction);
        acPaste = getActionByName(DefaultEditorKit.pasteAction);
        //取得JTextPane元件提供執行剪下、複製、貼上動作的Action物件

        acCut.putValue(Action.NAME, "剪下(T)"); //設定Action物件使用的名稱
        acCopy.putValue(Action.NAME, "複製(C)");
        acPaste.putValue(Action.NAME, "貼上(P)");

        acCut.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_T);
        acCopy.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_C);
        acPaste.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_P);
        //設定Action物件使用的記憶鍵

        acCut.setEnabled(false); //設定Action物件無效
        acCopy.setEnabled(false);

        mnEdit.add(acCut); //將Action物件加入功能表做為選項
        mnEdit.add(acCopy);
        mnEdit.add(acPaste);

        JMenu mnMusic = new JMenu("音樂(M)"); //宣告音樂
        mnMusic.setMnemonic(KeyEvent.VK_M); //設定檔案功能表使用的記憶鍵
        MusicMenu musicMenu = new MusicMenu(mnMusic,MDIEditor.this);

        JMenu mnAbout = new JMenu("關於(R)"); //宣告關於
        mnAbout.setMnemonic(KeyEvent.VK_R); //設定檔案功能表使用的記憶鍵
        AboutMenu aboutMenu = new AboutMenu(mnAbout);

        JMenuBar jmb = new JMenuBar(); //宣告功能表列物件
        setJMenuBar(jmb); //設定視窗框架使用的功能表列
        jmb.add(mnFile); //將功能表加入功能表列
        jmb.add(mnEdit);
        jmb.add(mnFontSize);
        jmb.add(wmWindow);
        jmb.add(mnMusic);
        jmb.add(mnAbout);

        tbFontSize = new JToolBar(); //新增工具列
        tbFontSize.add(tbnSize16); //將JToggleButton按鈕加入工具列
        tbFontSize.add(tbnSize18);
        tbFontSize.add(tbnSize20);

        JPanel plStatus = new JPanel(new GridLayout(1, 1)); //宣告做為狀態列的JPanel
        lbStatus = new JLabel("游標位置 : 第 0 個字元"); //宣告顯示訊息的標籤
        plStatus.add(lbStatus);	//將標籤加入JPanel容器		

        Container cp = getContentPane(); //取得內容面版
        cp.add(tbFontSize, BorderLayout.NORTH); //將工具列加入內容面版
        cp.add(dpPane); //將虛擬桌面加入內容面版
        cp.add(plStatus, BorderLayout.SOUTH); //將狀態列加入內容面版

        addWindowListener(wa); //註冊回應WindowEvent事件的監聽器

        //設定視窗預設的關閉動作、視窗大小, 並顯示視窗
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setSize(800, 600);
        setVisible(true);
    }

    //傳出用於容納內部框架的虛擬桌面
    public JDesktopPane getDesktopPane() {
        return dpPane;
    }

    //運用Action物件的名稱, 取得文字編輯面版提供的Action物件
    private Action getActionByName(String name) {

        Action[] actionsArray = fontSize.tifCurrent.getTextPane().getActions();
        //取得文字編輯面版提供的Action物件

        for (Action elm : actionsArray) {
            //運用比對名稱的方式, 取得Action物件的
            if (elm.getValue(Action.NAME).equals(name)) {
                return elm;
            }
        }
        return null;
    }

    //定義並宣告回應檔案功能表內選項被選取所觸發事件的監聽器
    ActionListener alFile = (ActionEvent e) -> {
        int result;
        try {
            //執行檔案開啟動作
            switch (e.getActionCommand()) {
                case "開啟舊檔(O)": {
                    JFileChooser fcOpen = new JFileChooser(
                            fontSize.tifCurrent.getFilePath());
                    //宣告JFileChooser物件
                    FileFilter fileFilter = NewFileFilter("TXT File", new String[]{"txt"});
                    fcOpen.addChoosableFileFilter(fileFilter);
                    //設定篩選檔案的類型
                    fcOpen.setDialogTitle("開啟舊檔"); //設定檔案選擇對話盒的標題
                    result = fcOpen.showOpenDialog(MDIEditor.this);
                    //顯示開啟檔案對話盒
                    if (result == JFileChooser.APPROVE_OPTION) { //使用者按下 確認 按鈕
                        File file = fcOpen.getSelectedFile(); //取得選取的檔案
                        fontSize.createInternalFrame(file.getPath(), file.getName());
                        //以取得的檔案建立TextInternalFrame物件
                    }
                    break;
                }
                case "新增(N)":
                    //新增文件
                    fontSize.createInternalFrame(); //建立沒有內容的TextInternalFrame物件
                    break;
                case "儲存檔案(S)":
                    //執行儲存檔案動作
                    String strPath = fontSize.tifCurrent.getFilePath();
                    //取得目前TextInternalFrame物件開啟檔案的路徑與名稱
                    if (!fontSize.tifCurrent.isNew()) {
                        //判斷TextInternalFrame物件開啟的是否為新的檔案
                        FileWriter fw = new FileWriter(strPath);
                        //建立輸出檔案的FileWriter物件
                        fontSize.tifCurrent.write(fw);
                    } else {
                        saveFile(strPath); //儲存檔案
                    }
                    break;
                case "另存新檔(A)":
                    saveFile(fontSize.tifCurrent.getFilePath()); //儲存檔案
                    break;
                case "PDF轉檔(Y)": {
                    JFileChooser fcOpen = new JFileChooser(
                            fontSize.tifCurrent.getFilePath());
                    //宣告JFileChooser物件
                    FileFilter fileFilter = NewFileFilter("PDF File", new String[]{"pdf"});
                    fcOpen.addChoosableFileFilter(fileFilter);
                    //設定篩選檔案的類型
                    fcOpen.setDialogTitle("選擇要轉檔的PDF"); //設定檔案選擇對話盒的標題
                    result = fcOpen.showOpenDialog(MDIEditor.this);
                    //顯示開啟檔案對話盒
                    if (result == JFileChooser.APPROVE_OPTION) { //使用者按下 確認 按鈕
                        AudioPlayer openYee =new AudioPlayer(file.getPath());
                        openYee.play();
                        File file = fcOpen.getSelectedFile(); //取得選取的檔案
                        try {
                            openYee.loadAudio(Yee);//載入yee
                        } catch (Exception YeeException) {
                            System.out.println(YeeException.toString());
                        }
                        openYee.play();
                        System.setProperty("apple.awt.UIElement", "true");
                        ExtractText extractor = new ExtractText();
                        String fi[] = {file.getPath()};
                        String fe, ff;
                        extractor.startExtraction(fi);
                        fe = fi[0].substring(0, fi[0].length() - 3) + "txt";//去尾
                        ff = file.getName().substring(0, file.getName().length() - 3) + "txt";//加上TXT
                        fontSize.createInternalFrame(fe, ff);//以取得的檔案建立TextInternalFrame物件
                    }
                    break;
                }
                case "結束(E)":
                    MDIEditor.this.processWindowEvent(
                            new WindowEvent(MDIEditor.this,
                                    WindowEvent.WINDOW_CLOSING));
                    //執行WindowEvent事件, 觸發MDIEditor視窗框架的關閉視窗事件
                    break;
            }
        } catch (IOException ioe) {
            System.err.println(ioe.toString());
        } catch (BadLocationException ble) {
            System.err.println("位置不正確");
        }
    };

    private void saveFile(String strPath) //儲存檔案
            throws IOException, BadLocationException {

        int pos = strPath.lastIndexOf("\\");

        String path = strPath.substring(0, pos + 1);
        String name = strPath.substring(pos + 1, strPath.length());
        JFileChooser fcSave = new JFileChooser(path);  //建立檔案選取對話盒
        fcSave.setSelectedFile(new File(name)); //設定選取的檔案

        FileFilter fileFilter = NewFileFilter("TXT File", new String[]{"txt"});
        fcSave.addChoosableFileFilter(fileFilter);
        //設定篩選檔案的類型

        fcSave.setDialogTitle("另存新檔"); //設定對話盒標題

        int result = fcSave.showSaveDialog(MDIEditor.this);
        //顯示檔案儲存對話盒

        if (result == JFileChooser.APPROVE_OPTION) {
            //使用者按下 確認 按鈕

            File file = fcSave.getSelectedFile(); //取得選取的檔案
            fontSize.tifCurrent.write(new FileWriter(file));
            //將文字編輯內部框架的內容輸出至FileWriter物件

            fontSize.tifCurrent.setFileName(file.getName()); //設定編輯檔案名稱
            fontSize.tifCurrent.setFilePath(file.getPath()); //設定編輯檔案路徑
        }
    }

    //建立過濾檔案選擇對話盒內檔案類型的物件
    public FileFilter NewFileFilter(final String desc, final String[] allowed_extensions) {
        return new FileFilter() {//建構子
            @Override
            public boolean accept(File f) {//若為資料夾傳回true
                if (f.isDirectory()) {
                    return true;
                }
                int pos = f.getName().lastIndexOf('.');//尋找檔案名稱內的"."號
                if (pos == -1) {
                    return false;
                } else {
                    String extension = f.getName().substring(pos + 1);//取得檔案名稱
                    for (String allowed_extension : allowed_extensions) {//從檔案名稱內取得副檔名字
                        if (extension.equalsIgnoreCase(allowed_extension)) {//判斷副檔名是否與檔案篩選物件的extension字串相同
                            return true;
                        }
                    }
                    return false;
                }
            }

            //傳回檔案篩選物件欲篩選檔案類型的描述字串
            @Override
            public String getDescription() {
                return desc;
            }
        };
    }

    //在關閉應用程式前, 運用監聽器判別程式內開啟的檔案是否已經儲存
    WindowAdapter wa = new WindowAdapter() {

        //回應視窗關閉事件的方法
        @Override
        public void windowClosing(WindowEvent e) {

            JInternalFrame[] ifAll = getDesktopPane().getAllFrames();
            //取得目前虛擬桌面內所有開啟的TextInternalFrame物件

            TextInternalFrame tifCurrent
                    = (TextInternalFrame) getDesktopPane().getSelectedFrame();
            //取得虛擬桌面目前選取的TextInternalFrame物件

            //判斷開啟的TextInternalFrame物件是否為0
            if (ifAll.length != 0) {

                //運用加強型for迴圈取得虛擬桌面內所有TextInternalFrame物件
                for (JInternalFrame elm : ifAll) {
                    try {
                        if (!((TextInternalFrame) elm).isChanged()) {
                            elm.setClosed(true); //關閉內部框架
                        } else {
                            int result
                                    = JOptionPane.showConfirmDialog(
                                            MDIEditor.this, "是否儲存?",
                                            "訊息", JOptionPane.YES_NO_CANCEL_OPTION,
                                            JOptionPane.INFORMATION_MESSAGE);
                            //顯示確認方塊

                            if (result == JOptionPane.NO_OPTION) //判斷是否按下 否 按鈕
                            {
                                elm.setClosed(true);
                            } else if (result == JOptionPane.CANCEL_OPTION) //判斷是否按下 取消 按鈕
                            {
                                return;
                            } else if (result == JOptionPane.YES_OPTION) {  //判斷是否按下 是 按鈕
                                String strPath = ((TextInternalFrame) elm).getFilePath();
                                //取得TextInternalFrame目前編輯檔案的路徑
                                //判斷TextInternalFrame目前編輯檔案是否為新的
                                if (!fontSize.tifCurrent.isNew()) {
                                    fontSize.tifCurrent.write(new FileWriter(strPath));
                                    //將TextInternalFrame的內容寫入FileWriter物件
                                } else {
                                    saveFile(strPath); //儲存檔案
                                }
                                elm.setClosed(true); //關閉內部框架
                            }
                        }
                    } catch (java.beans.PropertyVetoException pve) {
                        System.out.println(pve.toString());
                    } catch (IOException ioe) {
                        System.err.println(ioe.toString());
                    } catch (BadLocationException ble) {
                        System.err.println("位置不正確");
                    }
                }
            }

            ifAll = getDesktopPane().getAllFrames();
            //取得虛擬桌面目前開啟的所有內部框架

            if (ifAll.length == 0) //判斷內部框架的數目
            {
                System.exit(0); //結束應用程式
            }
        }
    };


}