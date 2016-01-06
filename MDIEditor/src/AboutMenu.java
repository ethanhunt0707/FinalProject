
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class AboutMenu {

    AboutMenu(JMenu mnAbout) {
        JMenuItem miIntroduce = new JMenuItem("Team Project"),
                miNatLee = new JMenuItem("00181034 李映澤"),
                miYuHang = new JMenuItem("00257122 張語航"),
                miFinianrry = new JMenuItem("00257138 吳彥澄"),
                miTommy = new JMenuItem("00257141 陳平揚"),
                miVic = new JMenuItem("00257148 陳威任");

        miNatLee.addActionListener(about);
        miYuHang.addActionListener(about);
        miFinianrry.addActionListener(about);
        miTommy.addActionListener(about);
        miVic.addActionListener(about);

        mnAbout.add(miIntroduce);
        mnAbout.addSeparator();
        mnAbout.addSeparator();
        mnAbout.add(miNatLee);
        mnAbout.add(miYuHang);
        mnAbout.add(miFinianrry);
        mnAbout.add(miTommy);
        mnAbout.add(miVic);
    }

    ActionListener about = (ActionEvent e) -> {
        try {
            String url = "";
            switch (e.getActionCommand()) {
                case "Team Project":
                    url = "https://github.com/NTOU-CS-JAVA2015/FinalProject";
                    break;
                case "00181034 李映澤":
                    url = "https://github.com/NatLee";
                    break;
                case "00257122 張語航":
                    url = "https://github.com/changyuhang";
                    break;
                case "00257138 吳彥澄":
                    url = "https://github.com/FinianWu";
                    break;
                case "00257141 陳平揚":
                    url = "https://github.com/ethanhunt0707";
                    break;
                case "00257148 陳威任":
                    url = "https://github.com/vic4113110631";
                    break;
            }
            Runtime.getRuntime().exec("cmd /c start " + url);
        } catch (IOException ioe) {
            System.err.println(ioe.toString());
        }
    };
}
