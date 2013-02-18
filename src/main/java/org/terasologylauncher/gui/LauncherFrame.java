package org.terasologylauncher.gui;

import org.terasologylauncher.BuildType;
import org.terasologylauncher.Settings;
import org.terasologylauncher.starter.TerasologyStarter;
import org.terasologylauncher.updater.GameData;
import org.terasologylauncher.updater.GameDownloader;
import org.terasologylauncher.util.TerasologyDirectories;
import org.terasologylauncher.util.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;

/**
 * @author Skaldarnar
 */
public class LauncherFrame extends JFrame implements ActionListener {
    public static final URL icon = LauncherFrame.class.getResource("/icon.png");

    private static final int FRAME_WIDTH    = 880;
    private static final int FRAME_HEIGHT   = 520;

    private static final int INFO_PANEL_WIDTH   = 600;
    private static final int INFO_PANEL_HEIGHT  = 300;

    private static final String SETTINGS_ACTION = "settings";
    private static final String CANCEL_ACTION   = "cancel";

    private static final String START_ACTION    = "start";
    private static final String DOWNLOAD_ACTION = "download";

    private JButton start;
    private JButton settings;
    private JButton cancel;

    private JButton facebook;
    private JButton github;
    private JButton gplus;
    private JButton twitter;
    private JButton youtube;

    private JProgressBar progressBar;

    private JLabel forums;
    private JLabel issues;
    private JLabel mods;

    private JPanel topPanel;
    private JPanel updatePanel;

    private JTextPane infoTextPane;

    private SettingsMenu settingsMenu;

    public LauncherFrame() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Terasology Launcher");
        setIconImage(Toolkit.getDefaultToolkit().getImage(icon));

        initComponents();

        updateStartButton();

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((dim.width - FRAME_WIDTH) / 2, (dim.height - FRAME_HEIGHT) / 2, FRAME_WIDTH, FRAME_HEIGHT);
        setSize(FRAME_WIDTH,FRAME_HEIGHT);
        setResizable(false);
        getContentPane().add(new BackgroundImage(FRAME_WIDTH,FRAME_HEIGHT));
    }

    private void initComponents() {
        int xShift = 0;
        int yShift = 0;
        if (this.isUndecorated()) {
            yShift += 30;
        }

        // Setup start button
        start = new TSButton("Start");
        start.setBounds(FRAME_WIDTH - 96 - 16 - xShift, FRAME_HEIGHT - 70 - 40 + yShift, 96, 32);
        start.setActionCommand(START_ACTION);
        start.addActionListener(this);

        // Options Button
        settings = new TSButton("Settings");
        settings.setBounds(FRAME_WIDTH - 96 - 16 - xShift, FRAME_HEIGHT - 70 - 2*40 + yShift, 96, 32);
        settings.setActionCommand(SETTINGS_ACTION);
        settings.addActionListener(this);

        // Cancel button
        cancel = new TSButton("Cancel");
        cancel.setBounds(FRAME_WIDTH - 96 - 16 - xShift, FRAME_HEIGHT - 70 + yShift, 96, 32);
        cancel.setActionCommand(CANCEL_ACTION);
        cancel.addActionListener(this);

        // Transparent top panel and content/update panel
        topPanel = new TransparentPanel(0.5f);
        topPanel.setBounds(0,0,FRAME_WIDTH, 96);

        updatePanel = new TransparentPanel(0.5f);
        updatePanel.setBounds(
                (FRAME_WIDTH - INFO_PANEL_WIDTH) / 2,
                (FRAME_HEIGHT - INFO_PANEL_HEIGHT) / 2,
                INFO_PANEL_WIDTH,
                INFO_PANEL_HEIGHT);

        infoTextPane = new JTextPane();
        infoTextPane.setFont(new Font("Arial", Font.PLAIN, 14));
        infoTextPane.setEditable(false);
        infoTextPane.setEnabled(false);
        infoTextPane.setHighlighter(null);
        infoTextPane.setOpaque(false);

        infoTextPane.setForeground(Color.WHITE);

        infoTextPane.setText("Lorem ipsum dolor sit amet, \n " +
                "consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore " +
                "\n magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores " +
                "\n et ea rebum. " +
                "\n Stet clita kasd gubergren, " +
                "\n no sea takimata sanctus est Lorem ipsum dolor sit amet. " +
                "\n Lorem ipsum dolor " +
                "sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore " +
                "\n \n \n magna aliquyam erat, sed diam voluptua. " +
                "\n At vero eos et accusam et justo duo dolores et ea rebum. " +
                "\n Stet clita kasd gubergren, " +
                "\n no sea takimata sanctus est " +
                "\n Lorem ipsum dolor sit amet.");

        //infoTextPane.setBounds(updatePanel.getX() + 8, updatePanel.getY() + 8, updatePanelWidth - 16, updatePanelHeight - 16);
        JScrollPane sp = new JScrollPane();
        sp.getViewport().add(infoTextPane);
        sp.getVerticalScrollBar().setOpaque(false);
        sp.getVerticalScrollBar().setUI(new TSScrollBarUI());
        sp.getViewport().setOpaque(false);
        sp.getVerticalScrollBar().setBorder(BorderFactory.createEmptyBorder());
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setOpaque(false);
        sp.setPreferredSize(new Dimension(INFO_PANEL_WIDTH-16, INFO_PANEL_HEIGHT-16));
        sp.setBounds(updatePanel.getX() + 8, updatePanel.getY() + 8, INFO_PANEL_WIDTH - 16, INFO_PANEL_HEIGHT - 16);

        // Terasology logo
        JLabel logo = new JLabel();
        logo.setBounds(8, 0, 400, 96);
        logo.setIcon(new ImageIcon(LauncherFrame.class.getResource("/logo.png")));

        // Forums link
        forums = new LinkJLabel("Forums", "http://forum.movingblocks.net");
        forums.setFont(forums.getFont().deriveFont(24f));
        forums.setBounds(480, 36, 96, 32);

        // Issues link
        issues = new LinkJLabel("Issues", "https://github.com/Movingblocks/Terasology/issues");
        issues.setFont(issues.getFont().deriveFont(24f));
        issues.setBounds(640, 36, 96, 32);

        // Mods
        mods = new LinkJLabel("Mods", "http://www.terasologymods.net");
        mods.setFont(mods.getFont().deriveFont(24f));
        mods.setBounds(FRAME_WIDTH-96-16-xShift, 36, 96, 32);

        // Progress Bar
        progressBar = new JProgressBar();
        progressBar.setBounds((FRAME_WIDTH / 2) - 200, FRAME_HEIGHT - 70 + yShift, 400, 23);
        progressBar.setVisible(false);
        progressBar.setStringPainted(true);

        // Social media
        github = new LinkJButton("https://github.com/Movingblocks/Terasology");
        github.setIcon(new ImageIcon(LauncherFrame.class.getResource("/github.png")));
        github.setRolloverIcon(new ImageIcon(LauncherFrame.class.getResource("/github_hover.png")));
        github.setBounds(8 + xShift, FRAME_HEIGHT - 70 + yShift, 32, 32);
        github.setBorder(null);

        youtube = new LinkJButton("http://www.youtube.com/user/blockmaniaTV");
        youtube.setIcon(new ImageIcon(LauncherFrame.class.getResource("/youtube.png")));
        youtube.setRolloverIcon(new ImageIcon(LauncherFrame.class.getResource("/youtube_hover.png")));
        youtube.setBounds(8 + 38 + xShift, FRAME_HEIGHT - 70 + yShift, 32, 32);
        youtube.setBorder(null);

        gplus = new LinkJButton("https://plus.google.com/b/103835217961917018533/103835217961917018533");
        gplus.setIcon(new ImageIcon(LauncherFrame.class.getResource("/gplus.png")));
        gplus.setRolloverIcon(new ImageIcon(LauncherFrame.class.getResource("/gplus_hover.png")));
        gplus.setBounds(8 + 38 * 2 + xShift, FRAME_HEIGHT - 70 + yShift, 32, 32);
        gplus.setBorder(null);

        facebook = new LinkJButton("https://www.facebook.com/Terasology");
        facebook.setIcon(new ImageIcon(LauncherFrame.class.getResource("/facebook.png")));
        facebook.setRolloverIcon(new ImageIcon(LauncherFrame.class.getResource("/facebook_hover.png")));
        facebook.setBounds(8 + 38 * 3 + xShift, FRAME_HEIGHT - 70 + yShift, 32, 32);
        facebook.setBorder(null);

        twitter = new LinkJButton("https://twitter.com/Terasology");
        twitter.setIcon(new ImageIcon(LauncherFrame.class.getResource("/twitter.png")));
        twitter.setRolloverIcon(new ImageIcon(LauncherFrame.class.getResource("/twitter_hover.png")));
        twitter.setBounds(8 + 38 * 4 + xShift, FRAME_HEIGHT - 70 + yShift, 32, 32);
        twitter.setBorder(null);

        Container contentPane = getContentPane();
        contentPane.setLayout(null);

        contentPane.add(logo);
        contentPane.add(forums);
        contentPane.add(issues);
        contentPane.add(mods);

        contentPane.add(start);
        contentPane.add(settings);
        contentPane.add(cancel);

        contentPane.add(github);
        contentPane.add(twitter);
        contentPane.add(facebook);
        contentPane.add(gplus);
        contentPane.add(youtube);

        contentPane.add(progressBar);

        contentPane.add(sp);

        contentPane.add(topPanel);
        contentPane.add(updatePanel);
    }

    public JProgressBar getProgressBar(){
        return progressBar;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JComponent) {
            action(e.getActionCommand(), (JComponent)e.getSource());
        }
    }

    private void action(String command, Component component) {
        if (command.equals(SETTINGS_ACTION)) {
            if (settingsMenu == null || !settingsMenu.isVisible()) {
                settingsMenu = new SettingsMenu();
                settingsMenu.setModal(true);
                settingsMenu.setVisible(true);
                settingsMenu.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        updateStartButton();
                    }
                });
            }
        } else if (command.equals(CANCEL_ACTION)) {
            this.dispose();
            System.exit(0);
        } else if (command.equals(START_ACTION)) {
            if (TerasologyStarter.startGame()){
                System.exit(0);
            } else {
                JOptionPane.showMessageDialog(null, "Could not start the game!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (command.equals(DOWNLOAD_ACTION)) {
            // cleanup the directories (keep savedWorlds and screen shots)
            cleanUp();
            // start a thread with the download
            final GameDownloader downloader = new GameDownloader(progressBar, this);
            downloader.execute();
        }
    }

    /**
     * Clean up the installation directory, that means delete all files and folders except of the files kept by
     * <tt>canBeDeleted</tt> method.
     */
    private void cleanUp() {
        for (File f : Utils.getWorkingDirectory().listFiles()){
            if (canBeDeleted(f)){
                if (f.isDirectory()){
                    deleteDirectory(f);
                } else {
                    f.delete();
                }
            }
        }
    }

    /**
     * Check if the file can be deleted on clean up action. The only files/directories kept are "SAVED_WORLDS",
     * "screens" and "launcher".
     *
     * @param f the file to check
     * @return true if the file can be deleted
     */
    private boolean canBeDeleted(File f) {
        if (f.getAbsolutePath().equals(TerasologyDirectories.getLauncherDir().getAbsolutePath())) {
            return false;
        }
        if (f.getAbsolutePath().equals(TerasologyDirectories.getSavedWorldsDir().getAbsolutePath())) {
            return false;
        }
        if (f.getAbsolutePath().equals(TerasologyDirectories.getScreenshotsDir().getAbsolutePath())) {
            return false;
        }
        if (f.getAbsolutePath().equals(TerasologyDirectories.getBackupDir().getAbsolutePath())) {
            return false;
        }
        return true;
    }

    /**
     * recursively deletes the directory and all of its content.
     * @param f
     */
    private void deleteDirectory(File f) {
        for (File sub : f.listFiles()){
            if (sub.isFile()) {
                sub.delete();
            } else {
                deleteDirectory(sub);
            }
        }
        f.delete();
    }

    /**
     * Updates the start button with regard to the selected settings, the internet connection and the installed game.
     * Changes the button text and action command ("start" or "download").
     */
    public void updateStartButton() {
        if (GameData.checkInternetConnection()) {
            // get the selected build type
            BuildType selectedType = Settings.getBuildType();
            // get the installed build type
            BuildType installedType = GameData.getInstalledBuildType();
            if (selectedType == installedType) {
                // check if update is possible
                // therefore, get the installed version no. and the upstream version number
                int installedVersion = GameData.getInstalledBuildVersion();
                int upstreamVersion  = GameData.getUpStreamVersion(installedType);
                int selectedVersion = Settings.getBuildVersion(installedType).equals("Latest") ? upstreamVersion
                        : Integer.parseInt(Settings.getBuildVersion(installedType));

                if (installedVersion == selectedVersion) {
                    // game can be started
                    start.setText("Start");
                    start.setActionCommand(START_ACTION);
                } else {
                    // differentiate between up- and downgrade
                    if (installedVersion < selectedVersion){
                        start.setText("Update");
                        start.setActionCommand(DOWNLOAD_ACTION);
                    } else {
                        start.setText("Downgrade");
                        start.setActionCommand(DOWNLOAD_ACTION);
                    }
                }
            } else {
                // download other build type
                start.setText("Download");
                start.setActionCommand(DOWNLOAD_ACTION);
            }
        } else {
            if (GameData.isGameInstalled()){
                // installed game can be started
                start.setText("Start");
                start.setActionCommand(START_ACTION);
            } else {
                // no game installed, and no way to download it...
                start.setEnabled(false);
            }
        }
    }
}