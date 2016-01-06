
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.spi.mpeg.sampled.file.MpegAudioFileReader;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author yuhang
 */
public class MusicMenu {

    boolean musicFlag = false;//判斷是否開啟過音樂
    JDesktopPane dpPane;
    TextInternalFrame tifCurrent;
    boolean mp3 = false;//判斷為mp3檔
    MDIEditor MDIEditor;
    ScheduledExecutorService scheduler;
    boolean scheduling = false;//判斷是否排程
    PlayMP3 player = null;
    AudioPlayer audio = null;//音樂控制項

    MusicMenu(JMenu mnMusic, MDIEditor MDIEditorin) {
        MDIEditor = MDIEditorin;
        dpPane = MDIEditorin.dpPane;
        tifCurrent = MDIEditorin.fontSize.tifCurrent;
        JMenuItem miOpenMusic = new JMenuItem("開啟音樂檔(O)", KeyEvent.VK_O),
                miPause = new JMenuItem("暫停(P)", KeyEvent.VK_P),
                miContinue = new JMenuItem("繼續(K)", KeyEvent.VK_K),
                miStop = new JMenuItem("停止(T)", KeyEvent.VK_T);

        miOpenMusic.addActionListener(music); //為功能表選項加上監聽器
        miPause.addActionListener(music);
        miContinue.addActionListener(music);
        miStop.addActionListener(music);

        mnMusic.add(miOpenMusic); //將選項加入檔案功能表
        mnMusic.addSeparator();
        mnMusic.add(miPause);
        mnMusic.add(miContinue);
        mnMusic.addSeparator();
        mnMusic.add(miStop);
    }

    ActionListener music = (ActionEvent e) -> {
        int result;
        switch (e.getActionCommand()) {
            case "開啟音樂檔(O)":
                if (musicFlag) {
                    JOptionPane.showMessageDialog(dpPane, "醒醒吧！你沒聽到聲音嗎？\n或許真的沒聽到？請停止播放再開檔！");
                }
                if (!musicFlag) {
                    JFileChooser fcOpen = new JFileChooser(
                            tifCurrent.getFilePath());
                    //宣告JFileChooser物件
                    FileFilter fileFilter = MDIEditor.NewFileFilter("Media Files", new String[]{"mp3", "au", "aiff", "wav"});
                    fcOpen.addChoosableFileFilter(fileFilter);
                    //設定篩選檔案的類型
                    fcOpen.setDialogTitle("開啟WAV檔"); //設定檔案選擇對話盒的標題
                    result = fcOpen.showOpenDialog(MDIEditor);
                    //顯示開啟檔案對話盒
                    if (result == JFileChooser.APPROVE_OPTION) {
                        //使用者按下 確認 按鈕
                        musicFlag = true;
                        mp3 = false;
                        File file = fcOpen.getSelectedFile(); //取得選取的檔案
                        String strCmp = file.getPath().substring(file.getPath().length() - 3, file.getPath().length());
                        if (strCmp.equals("mp3") || strCmp.equals("MP3") || strCmp.equals("Mp3") || strCmp.equals("mP3")) {
                            mp3 = true;
                            try {
                                AudioFileFormat baseFileFormat = new MpegAudioFileReader().getAudioFileFormat(file);
                                Map properties = baseFileFormat.properties();
                                long duration = (long) properties.get("duration");//mp3長度

                                scheduler = Executors.newSingleThreadScheduledExecutor();
                                scheduling = true;
                                final ScheduledFuture future = scheduler.scheduleAtFixedRate(new Runnable() {
                                    //設定排程
                                    @Override
                                    public void run() {
                                        // 排程工作
                                        if (scheduling) {
                                            try {
                                                FileInputStream fis = new FileInputStream(file.getPath());
                                                player = new PlayMP3(fis);
                                                player.play();
                                            } catch (FileNotFoundException | JavaLayerException ex) {
                                                System.out.println(ex.toString());
                                            }
                                        }
                                    }
                                }, 0, duration + 5000, TimeUnit.MICROSECONDS);//0微秒開始執行，每duration+5000微秒執行一次

                            } catch (UnsupportedAudioFileException | IOException ex) {
                                System.out.println(ex.toString());
                            }
                        } else {
                            audio = new AudioPlayer();
                            audio.loadAudio(file.getPath());
                            audio.setPlayCount(0);//0為持續播放
                            audio.play();
                        }
                    }
                }
                break;
            case "暫停(P)":
                if (musicFlag) {
                    if (mp3) {
                        scheduling = false;
                        player.pause();
                    } else {
                        audio.pause();
                    }
                } else {
                    JOptionPane.showMessageDialog(dpPane, "醒醒吧！你還沒開音樂！");
                }
                break;
            case "繼續(K)":
                if (musicFlag) {
                    if (mp3) {
                        scheduling = true;
                        player.resume();
                    } else {
                        audio.resume();
                    }
                } else {
                    JOptionPane.showMessageDialog(dpPane, "醒醒吧！你還沒開音樂！");
                }
                break;
            case "停止(T)":
                if (musicFlag) {
                    scheduler.shutdownNow();
                    player.stop();
                    if (mp3) {
                    } else {
                        audio.close();
                    }
                    JOptionPane.showMessageDialog(dpPane, "要再次播放請重新選擇音樂！");
                    musicFlag = false;
                } else {
                    JOptionPane.showMessageDialog(dpPane, "醒醒吧！你還沒開音樂！");
                }
                break;
        }
    };
}