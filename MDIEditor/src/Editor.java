
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import static javax.swing.Action.MNEMONIC_KEY;
import static javax.swing.Action.SHORT_DESCRIPTION;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.text.DefaultEditorKit;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author yuhang
 */
public class Editor {

    TextInternalFrame tifCurrent;
    JToolBar tbFontSize;
    MDIEditor MDIEditor;
    WindowMenu wmWindow;

    Editor(JMenu mnFontSize, MDIEditor MDIEditorin, WindowMenu wmWindowin) {

        //tifCurrent = MDIEditorin.tifCurrent;
        MDIEditor = MDIEditorin;
        tbFontSize = MDIEditorin.tbFontSize;
        wmWindow = wmWindowin;
        java.net.URL imgSize16URL = MDIEditor.class.getResource("/icon/size16.png");
        java.net.URL imgSize18URL = MDIEditor.class.getResource("/icon/size18.png");
        java.net.URL imgSize20URL = MDIEditor.class.getResource("/icon/size20.png");
        Editor.EditorAction fsaSize16 = new Editor.EditorAction("16(S)", new ImageIcon(imgSize16URL), "設定字體大小為16", KeyEvent.VK_S);
        Editor.EditorAction fsaSize18 = new Editor.EditorAction("18(M)", new ImageIcon(imgSize18URL), "設定字體大小為18", KeyEvent.VK_M);
        Editor.EditorAction fsaSize20 = new Editor.EditorAction("20(L)", new ImageIcon(imgSize20URL), "設定字體大小為20", KeyEvent.VK_L);
        //宣告執行字級大小設定動作的Action物件

        MDIEditorin.cbmiSize16 = new JCheckBoxMenuItem(fsaSize16);
        MDIEditorin.cbmiSize18 = new JCheckBoxMenuItem(fsaSize18);
        MDIEditorin.cbmiSize20 = new JCheckBoxMenuItem(fsaSize20);
        //以執行字級大小設定之Action物件建立核取方塊選項

        MDIEditorin.cbmiSize16.setIcon(null); //設定核取方塊選項不使用圖示
        MDIEditorin.cbmiSize18.setIcon(null);
        MDIEditorin.cbmiSize20.setIcon(null);

        MDIEditorin.cbmiSize16.setState(true); //設定選取代表16字級的核取方塊選項

        mnFontSize.add(MDIEditorin.cbmiSize16); //將核取方塊選項加入功能表
        mnFontSize.add(MDIEditorin.cbmiSize18);
        mnFontSize.add(MDIEditorin.cbmiSize20);

        ButtonGroup bgSize = new ButtonGroup(); //宣告按鈕群組
        bgSize.add(MDIEditorin.cbmiSize16); //將核取方塊選項加入按鈕群組
        bgSize.add(MDIEditorin.cbmiSize18);
        bgSize.add(MDIEditorin.cbmiSize20);

        MDIEditorin.tbnSize16 = new JToggleButton(fsaSize16);
        MDIEditorin.tbnSize18 = new JToggleButton(fsaSize18);
        MDIEditorin.tbnSize20 = new JToggleButton(fsaSize20);
        //以執行字級大小設定的Action物件, 宣告工具列的JToggleButton按鈕

        MDIEditorin.tbnSize16.setActionCommand("16(S)");
        MDIEditorin.tbnSize18.setActionCommand("18(M)");
        MDIEditorin.tbnSize20.setActionCommand("20(L)");
        //因為按鈕不顯示字串,故必須設定動作命令字串, 以便於回應事件時判別

        MDIEditorin.tbnSize16.setText(null); //設定JToggleButton按鈕不顯示字串
        MDIEditorin.tbnSize18.setText(null);
        MDIEditorin.tbnSize20.setText(null);

        MDIEditorin.tbnSize16.setSelected(true);//設定選取代表16字級的JToggleButton按鈕

        ButtonGroup bgToolBar = new ButtonGroup(); //宣告按鈕群組
        bgToolBar.add(MDIEditorin.tbnSize16); //將JToggleButton按鈕加入按鈕群組
        bgToolBar.add(MDIEditorin.tbnSize18);
        bgToolBar.add(MDIEditorin.tbnSize20);
    }

    //定義執行文字字級設定的Action物件
    class EditorAction extends AbstractAction {

        public EditorAction(String text, ImageIcon icon,
                String desc, Integer mnemonic) {
            super(text, icon); //呼叫基礎類別建構子
            putValue(SHORT_DESCRIPTION, desc); //設定提示字串
            putValue(MNEMONIC_KEY, mnemonic); //設定記憶鍵
        }

        @Override
        public void actionPerformed(ActionEvent e) { //回應事件的執行動作
            //依照動作命令字串判別欲執行的動作
            switch (e.getActionCommand()) {
                case "20(L)":
                    tifCurrent.setFontSize(20);
                    //設定文字編輯面版使用20級字
                    MDIEditor.cbmiSize20.setSelected(true); //設定對應的控制項為選取
                    MDIEditor.tbnSize20.setSelected(true);
                    break;
                case "18(M)":
                    tifCurrent.setFontSize(18);
                    MDIEditor.cbmiSize18.setSelected(true);
                    MDIEditor.tbnSize18.setSelected(true);
                    break;
                default:
                    //預設16級字
                    tifCurrent.setFontSize(16);
                    MDIEditor.cbmiSize16.setSelected(true);
                    MDIEditor.tbnSize16.setSelected(true);
                    break;
            }
        }
    }

    //建立文字編輯內部框架
    public void createInternalFrame(String... strArgs) {

        //依照是否傳入參數決定呼叫的TextInternalFrame類別建構子
        if (strArgs.length == 0) {
            tifCurrent = new TextInternalFrame();
        } else {
            tifCurrent = new TextInternalFrame(strArgs[0], strArgs[1]);
        }

        tifCurrent.addCaretListener(cl);
        //註冊回應游標CaretEvent事件的監聽器

        tifCurrent.addInternalFrameListener(ifl);
        //註冊回應InternalFrameEvent事件的監聽器

        JCheckBoxMenuItem cbmiWindow = tifCurrent.getMenuItem();
        //取得代表完成建立之TextInternalFrame物件的核取方塊選項

        wmWindow.add(cbmiWindow, tifCurrent);
        //將核取方塊選項與對應的TextInternalFrame物件新增至視窗功能表

        MDIEditor.dpPane.add(tifCurrent);
        //將完成建立的TextInternalFrame物件加入虛擬桌面

        int FrameCount = MDIEditor.dpPane.getAllFrames().length;
        //取得虛擬桌面內TextInternalFrame物件的個數

        tifCurrent.setLocation(20 * (FrameCount - 1), 20 * (FrameCount - 1));
        //設定TextInternalFrame物件所顯示文字編輯視窗框架左上角在虛擬桌面的座標

        try {
            tifCurrent.setSelected(true);
            //設定選取完成建立的TextInternalFrame物件
        } catch (java.beans.PropertyVetoException pve) {
            System.out.println(pve.toString());
        }
    }

    //定義並宣告回應InternalFrame事件的監聽器
    InternalFrameAdapter ifl = new InternalFrameAdapter() {

        //當內部框架取得游標焦點觸發事件將由此方法回應
        @Override
        public void internalFrameActivated(InternalFrameEvent e) {

            tifCurrent = (TextInternalFrame) e.getInternalFrame();
            //取得觸發InternalFrame事件的TextInternalFrame物件

            tifCurrent.getMenuItem().setSelected(true);
            //設定視窗功能表內代表此TextInternalFrame物件的核取方塊選項為選取

            //取得TextInternalFrame物件顯示內容使用的字級大小
            switch (tifCurrent.getFontSize()) {
                case 16:
                    MDIEditor.cbmiSize16.setSelected(true); //設定對應的控制項為選取
                    MDIEditor.tbnSize16.setSelected(true);
                    break;
                case 18:
                    MDIEditor.cbmiSize18.setSelected(true);
                    MDIEditor.tbnSize18.setSelected(true);
                    break;
                case 20:
                    MDIEditor.cbmiSize20.setSelected(true);
                    MDIEditor.tbnSize20.setSelected(true);
                    break;
            }

        }

        //當內部框架正在關閉時所觸發事件將由此方法回應
        @Override
        public void internalFrameClosing(InternalFrameEvent e) {
            wmWindow.remove(tifCurrent.getMenuItem());
            //移除視窗功能表內代表目前執行編輯之TextInternalFrame物件的選項
        }
    };

    //定義並宣告回應CaretEvent事件的監聽器
    CaretListener cl = new CaretListener() {

        //移動游標位置時, 將由此方法回應
        @Override
        public void caretUpdate(CaretEvent e) {

            if (e.getDot() != e.getMark()) {
                MDIEditor.lbStatus.setText("目前位置 : 第 " + e.getDot()
                        + " 個字元" + ", 選取範圍 : " + e.getDot() + "至" + e.getMark());
                //設定狀態列內的文字

                MDIEditor.acCut.setEnabled(true);
                MDIEditor.acCopy.setEnabled(true);
                //設定執行剪下與複製動字的Action元件為有效
            } else {
                MDIEditor.lbStatus.setText("目前位置 : 第 " + e.getDot() + " 個字元");
                //設定狀態列內的文字

                MDIEditor.acCut.setEnabled(false);
                MDIEditor.acCopy.setEnabled(false);
                //設定執行剪下與複製動字的Action元件為無效
            }
        }
    };
}