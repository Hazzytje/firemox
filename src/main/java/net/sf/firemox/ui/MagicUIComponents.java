/*
 *   Firemox is a turn based strategy simulator
 *   Copyright (C) 2003-2007 Fabrice Daugan
 *
 *   This program is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by the Free 
 * Software Foundation; either version 2 of the License, or (at your option) any
 * later version.
 *
 *   This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
 * details.
 *
 *   You should have received a copy of the GNU General Public License along  
 * with this program; if not, write to the Free Software Foundation, Inc., 
 * 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.sf.firemox.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.Locale;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.ToolTipManager;

import net.sf.firemox.AbstractMainForm;
import net.sf.firemox.Magic;
import net.sf.firemox.clickable.action.ChosenCostPanel;
import net.sf.firemox.clickable.mana.Mana;
import net.sf.firemox.clickable.target.TargetFactory;
import net.sf.firemox.clickable.target.card.CardFactory;
import net.sf.firemox.clickable.target.player.Player;
import net.sf.firemox.stack.EventManager;
import net.sf.firemox.token.IdConst;
import net.sf.firemox.tools.Configuration;
import net.sf.firemox.tools.MToolKit;
import net.sf.firemox.tools.Picture;
import net.sf.firemox.tools.TimerTarget;
import net.sf.firemox.tools.WebBrowser;
import net.sf.firemox.ui.component.CardPropertiesPanel;
import net.sf.firemox.ui.component.ChatArea;
import net.sf.firemox.ui.component.EditorPane;
import net.sf.firemox.ui.component.LogArea;
import net.sf.firemox.ui.component.MessageButton;
import net.sf.firemox.ui.component.SplashScreen;
import net.sf.firemox.ui.component.TableTop;
import net.sf.firemox.ui.i18n.Language;
import net.sf.firemox.ui.i18n.LanguageManager;
import net.sf.firemox.ui.layout.BorderLayout2;
import net.sf.firemox.zone.PopupManager;
import net.sf.firemox.zone.ZoneManager;

/**
 * @author <a href="mailto:fabdouglas@users.sourceforge.net">Fabrice Daugan </a>
 * @since 0.54.17
 */
public abstract class MagicUIComponents extends AbstractMainForm implements
		MUIManager, ActionListener, MouseListener, WindowListener {

	/**
	 * The timer panel.
	 */
	private TimerGlassPane timerPanel;

	/**
	 * The timer target of Firemox
	 */
	public static TimerTarget targetTimer;

	/**
	 * The timer of Firemox
	 */
	public static Timer timer;

	/**
	 * Set the glass pane of the main form.
	 */
	public void updateTimerPanel() {
		setGlassPane(timerPanel);
	}

	/**
	 * 
	 */
	public MagicUIComponents() {
		super("Firemox");
		magicForm = (Magic) this;

		// Set the non-maximized size
		Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
		getRootPane().setPreferredSize(
				new Dimension(screenDim.width - 100, screenDim.height - 100));
		getRootPane().setMinimumSize(getRootPane().getPreferredSize());
		setUndecorated(frameDecorated);
		addWindowListener(this);
		timerPanel = new TimerGlassPane();
		timerPanel.setVisible(false);
		targetTimer = new TimerTarget(timerPanel);
		timer = new Timer(1000, targetTimer);
		updateTimerPanel();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	protected void initComponents() {
		try {
			setIconImage(Picture.loadImage(IdConst.IMAGES_DIR + "mp.gif"));
		} catch (Exception e) {
			// IGNORING
		}
		MToolKit.tbsName = Configuration.getString("lastTBS", IdConst.TBS_DEFAULT);
		moreThemeMenu = new JMenuItem(LanguageManager.getString("morethemes"));

		// Player status
		waitingLabel = new JLabel();
		waitingLabel.setHorizontalAlignment(SwingConstants.RIGHT);

		turnsLbl = new JLabel();
		turnsLbl.setHorizontalTextPosition(SwingConstants.LEFT);
		turnsLbl.setMaximumSize(new Dimension(70, 16));
		turnsLbl.setPreferredSize(new Dimension(70, 16));

		final JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.X_AXIS));
		infoPanel.setMaximumSize(new Dimension(2200, 20));
		infoPanel.add(turnsLbl);

		backgroundBtn = new MessageButton(UIHelper.getIcon("smlquestion1.gif"),
				UIHelper.getIcon("smlquestion2.gif"));
		backgroundBtn.setVisible(false);

		moreInfoLbl = new JLabel();
		moreInfoLbl.setHorizontalTextPosition(SwingConstants.RIGHT);
		moreInfoLbl.setMaximumSize(new Dimension(2200, 16));
		moreInfoLbl.setForeground(Color.BLUE);
		infoPanel.add(moreInfoLbl);

		playerTabbedPanel = new JTabbedPane(SwingConstants.BOTTOM,
				JTabbedPane.SCROLL_TAB_LAYOUT);

		// the card's "more info" tabbed panel containing the preview panel
		JTabbedPane previewTabbedPanel = null;
		previewPanel = new JPanel(new FlowLayout(0, 0, 0));
		previewPanel.setBackground(Color.black);
		chosenCostPanel = new ChosenCostPanel();
		databasePanel = new CardPropertiesPanel();
		if (speparateAvatar) {
			previewTabbedPanel = new JTabbedPane(SwingConstants.BOTTOM,
					JTabbedPane.SCROLL_TAB_LAYOUT);
			previewTabbedPanel.add(previewPanel, UIHelper.getIcon("zoom.gif"));
			previewTabbedPanel.add(chosenCostPanel, UIHelper
					.getIcon("chosenaction.gif"));
			previewTabbedPanel.add(databasePanel, UIHelper
					.getIcon("databasecard.gif"));
			previewTabbedPanel.setToolTipTextAt(previewTabbedPanel
					.indexOfComponent(previewPanel), "<html>"
					+ LanguageManager.getString("zoom.tooltip"));
			previewTabbedPanel.setToolTipTextAt(previewTabbedPanel
					.indexOfComponent(chosenCostPanel), "<html>"
					+ LanguageManager.getString("chosen.tooltip"));
			previewTabbedPanel.setToolTipTextAt(previewTabbedPanel
					.indexOfComponent(databasePanel), "<html>"
					+ LanguageManager.getString("database.tooltip"));
		} else {
			playerTabbedPanel.add(previewPanel, UIHelper.getIcon("zoom.gif"));
			playerTabbedPanel.add(chosenCostPanel, UIHelper
					.getIcon("chosenaction.gif"));
			playerTabbedPanel.setToolTipTextAt(playerTabbedPanel
					.indexOfComponent(previewPanel), "<html>"
					+ LanguageManager.getString("zoom.tooltip"));
			playerTabbedPanel.setToolTipTextAt(playerTabbedPanel
					.indexOfComponent(chosenCostPanel), "<html>"
					+ LanguageManager.getString("chosen.tooltip"));
			playerTabbedPanel
					.add(databasePanel, UIHelper.getIcon("databasecard.gif"));
			playerTabbedPanel.setToolTipTextAt(playerTabbedPanel
					.indexOfComponent(databasePanel), "<html>"
					+ LanguageManager.getString("database.tooltip"));
		}

		// the chat
		chatPanel = new JPanel(new BorderLayout());

		// the chat options
		final JPanel chatOptions = new JPanel();
		chatHistoryText = new ChatArea(new FlowLayout(FlowLayout.LEFT, 2, 0));
		final JButton clearChatButton = new JButton(UIHelper.getIcon("clear.gif"));
		clearChatButton.setActionCommand("clear");
		clearChatButton.addActionListener(chatHistoryText);
		clearChatButton.setPreferredSize(new Dimension(18, 18));
		clearChatButton.setFocusPainted(false);
		chatOptions.add(clearChatButton);
		final JToggleButton chatTimeButton = new JToggleButton(UIHelper
				.getIcon("time.gif"), Configuration.getBoolean("chatdisptime", false));
		chatTimeButton.setActionCommand("disptime");
		chatTimeButton.addActionListener(chatHistoryText);
		chatTimeButton.setPreferredSize(new Dimension(18, 18));
		chatTimeButton.setFocusPainted(false);
		chatOptions.add(chatTimeButton);
		final JToggleButton chatLockButton = new JToggleButton(UIHelper
				.getIcon("lock.gif"), Configuration.getBoolean("chatlocked", false));
		chatLockButton.setActionCommand("lock");
		chatLockButton.addActionListener(chatHistoryText);
		chatLockButton.setFocusPainted(false);
		chatLockButton.setPreferredSize(new Dimension(18, 18));
		chatOptions.add(chatLockButton);
		chatOptions.setMaximumSize(new Dimension(2000, 18));
		chatOptions.add(chatLockButton);
		chatPanel.add(chatOptions, BorderLayout.NORTH);

		// the chat history
		final JScrollPane chatSPanel = new JScrollPane();
		chatHistoryText.setLocked(chatLockButton.isSelected());
		chatHistoryText.setDispTime(chatTimeButton.isSelected());
		chatSPanel
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		chatSPanel
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		chatSPanel.setAutoscrolls(true);
		MToolKit.addOverlay(chatSPanel);
		chatSPanel.setViewportView(chatHistoryText);
		chatPanel.add(chatSPanel, BorderLayout.CENTER);

		// the text filed
		sendTxt = new JTextField();
		final JPanel messagePanel = new JPanel(new BorderLayout());
		messagePanel.setMaximumSize(new Dimension(200, 18));
		messagePanel.add(sendTxt, BorderLayout.CENTER);
		sendButton = new JButton(UIHelper.getIcon("ok.gif"));
		sendButton.setBorderPainted(false);
		sendButton.setFocusPainted(false);
		sendButton.setPreferredSize(new Dimension(18, 18));
		sendButton.addActionListener(this);
		messagePanel.add(sendButton, BorderLayout.EAST);
		chatPanel.add(messagePanel, BorderLayout.SOUTH);

		playerTabbedPanel.add(chatPanel, UIHelper.getIcon("chat-old.gif"));
		playerTabbedPanel.setToolTipTextAt(playerTabbedPanel
				.indexOfComponent(chatPanel), "<html>"
				+ LanguageManager.getString("chat.tooltip"));

		// the game log panel : a JTextArea
		final JPanel logPanel = new JPanel(new BorderLayout());

		// the log options
		final JPanel logOptions = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
		logListing = new LogArea();
		final JButton clearButton = new JButton(UIHelper.getIcon("clear.gif"));
		clearButton.setActionCommand("clear");
		clearButton.addActionListener(logListing);
		clearButton.setPreferredSize(new Dimension(18, 18));
		clearButton.setFocusPainted(false);
		logOptions.add(clearButton);
		final JToggleButton timeButton = new JToggleButton(UIHelper
				.getIcon("time.gif"), Configuration.getBoolean("logdisptime", false));
		timeButton.setActionCommand("disptime");
		timeButton.addActionListener(logListing);
		timeButton.setPreferredSize(new Dimension(18, 18));
		timeButton.setFocusPainted(false);
		logOptions.add(timeButton);
		final JToggleButton lockButton = new JToggleButton(UIHelper
				.getIcon("lock.gif"), Configuration.getBoolean("loglocked", false));
		lockButton.setActionCommand("lock");
		lockButton.addActionListener(logListing);
		lockButton.setPreferredSize(new Dimension(18, 18));
		lockButton.setFocusPainted(false);
		logOptions.add(lockButton);
		logPanel.add(logOptions, BorderLayout.NORTH);

		// the log history
		final JScrollPane logSPanel = new JScrollPane(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		logListing.setLocked(lockButton.isSelected());
		logListing.setDispTime(timeButton.isSelected());

		logOptions.setMaximumSize(new Dimension(2000, 18));
		logSPanel.setAutoscrolls(true);
		MToolKit.addOverlay(logSPanel);
		logSPanel.setViewportView(logListing);
		logPanel.add(logSPanel);

		playerTabbedPanel.add(logPanel, UIHelper.getIcon("menu_tools_log.gif"));
		playerTabbedPanel.setToolTipTextAt(playerTabbedPanel
				.indexOfComponent(logPanel), "<html>"
				+ LanguageManager.getString("log.tooltip"));

		// Main menubar
		JMenuBar magicMenu = new JMenuBar();
		JMenu gameMenu = UIHelper.buildMenu("menu_game", 'g');
		JMenu newGameMenu = UIHelper.buildMenu("menu_game_new", 'n');

		// "Join" menu Item
		JMenuItem joinMenu = UIHelper.buildMenu("menu_game_new_client", 'j', this);
		joinMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
		newGameMenu.add(joinMenu);

		// "Create" menu item
		JMenuItem createMenu = UIHelper
				.buildMenu("menu_game_new_server", 'c', this);
		createMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2,
				InputEvent.SHIFT_MASK));
		newGameMenu.add(createMenu);

		gameMenu.add(newGameMenu);

		// "Decline" button
		skipButton = UIHelper.buildButton("menu_game_skip", this);
		skipButton.setPreferredSize(new Dimension(Player.PLAYER_SIZE_HEIGHT,
				Player.PLAYER_SIZE_HEIGHT));

		// "Decline" menu item
		skipMenu = UIHelper.buildMenu("menu_game_skip", 's', this);
		skipMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE,
				InputEvent.SHIFT_MASK));
		gameMenu.add(skipMenu);

		gameMenu.add(UIHelper.buildMenu("menu_game_proposedraw", this));
		gameMenu.add(UIHelper.buildMenu("menu_game_disconnect", this));
		gameMenu.add(UIHelper.buildMenu("menu_game_proxy", this));
		gameMenu.add(new JSeparator());

		// "Exit" menu item
		final JMenuItem exitMenu = UIHelper.buildMenu("menu_game_exit", 'q', this);
		exitMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4,
				InputEvent.ALT_MASK));
		gameMenu.add(exitMenu);
		magicMenu.add(gameMenu);

		optionMenu = UIHelper.buildMenu("menu_options", 'o');
		final JMenu lookNFeelMenu = UIHelper.buildMenu("menu_lf");

		// "Reverse opponent arts" option
		reverseArtCheck = new JCheckBoxMenuItem(LanguageManager
				.getString("reverseart"),
				Configuration.getBoolean("reverseArt", true) ? UIHelper
						.getIcon("reverse.gif") : UIHelper.getIcon("reverseoff.gif"),
				Configuration.getBoolean("reverseArt", true));
		reverseArtCheck.setToolTipText("<html>"
				+ LanguageManager.getString("reverseart.tooltip"));
		reverseSideCheck = new JCheckBoxMenuItem(LanguageManager
				.getString("reverseside"), Configuration.getBoolean("reverseSide",
				false) ? UIHelper.getIcon("reverseside.gif") : UIHelper
				.getIcon("reversesideoff.gif"), Configuration.getBoolean("reverseSide",
				false));
		reverseSideCheck.setToolTipText("<html>"
				+ LanguageManager.getString("reverseside.tooltip"));
		reverseArtCheck.addActionListener(this);
		reverseSideCheck.addActionListener(this);
		lookNFeelMenu.add(reverseArtCheck);
		lookNFeelMenu.add(reverseSideCheck);

		// "Card border" option
		final String[] colors = { "black", "white", "gold", "auto" };
		final String currentColor = Configuration.getString("border-color",
				colors[3]);
		cardBorderMenu = new JMenu(LanguageManager.getString("border-color"));
		final ButtonGroup group4 = new ButtonGroup();
		boolean found = false;
		for (int i = 0; i < colors.length; i++) {
			String codeColor = "border-" + colors[i];
			JRadioButtonMenuItem colorItem = new JRadioButtonMenuItem(LanguageManager
					.getString(codeColor), UIHelper.getIcon(codeColor + ".gif"));
			colorItem.setActionCommand(codeColor);
			colorItem.addActionListener(this);
			if (!found && currentColor.equals(colors[i])) {
				colorItem.setSelected(true);
				found = true;
			}
			group4.add(colorItem);
			cardBorderMenu.add(colorItem);
		}
		if (!found) {
			((JRadioButtonMenuItem) cardBorderMenu.getComponent(0)).setSelected(true);
		}
		lookNFeelMenu.add(cardBorderMenu);

		// Custom colors of Power/Toughness
		lookNFeelMenu.add(UIHelper.buildMenu("menu_lf_powerToughnessColor", this));

		// Random angle option
		final JCheckBoxMenuItem randomAngleMenu = new JCheckBoxMenuItem(
				LanguageManager.getString("menu_lf_randomAngle"), UIHelper
						.getIcon("menu_lf_randomAngle.gif"), Configuration.getBoolean(
						"randomAngle", false));
		randomAngleMenu.addActionListener(this);
		lookNFeelMenu.add(randomAngleMenu);

		// "Sound" option
		soundMenu = new JCheckBoxMenuItem(LanguageManager.getString("enablesound"),
				Configuration.getBoolean("sound", false) ? UIHelper
						.getIcon("sound.gif") : UIHelper.getIcon("soundoff.gif"),
				Configuration.getBoolean("sound", false));
		soundMenu.addActionListener(this);
		lookNFeelMenu.add(soundMenu);

		// Tooltip delay
		// TODO factor this code with the one of Magic.class
		final ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
		final int delay = Configuration.getInt("initialdelay", 800);
		final int dismissDelay = Configuration.getInt("dismissdelay", 10000);
		if (delay == 0) {
			toolTipManager.setEnabled(false);
		} else {
			toolTipManager.setInitialDelay(delay);
		}
		toolTipManager.setDismissDelay(dismissDelay);
		initialdelayMenu = new JMenuItem(LanguageManager.getString("initialdelay")
				+ " : " + delay + " ms", UIHelper.getIcon("initialdelay.gif"));
		dismissdelayMenu = new JMenuItem(LanguageManager.getString("dismissdelay")
				+ " : " + dismissDelay + " ms", UIHelper.getIcon("initialdelay.gif"));

		lookNFeelMenu.add(initialdelayMenu);
		lookNFeelMenu.add(dismissdelayMenu);

		// "Themes" menu
		themeMenu = new JMenu(LanguageManager.getString("theme"));
		themeMenu.setIcon(UIHelper.getIcon("theme.gif"));
		themeMenu.add(new JSeparator());
		themeMenu.add(moreThemeMenu);
		lookNFeelMenu.add(themeMenu);

		// "Language" menu
		languageMenu = new JMenu(LanguageManager.getString("menu_lf_language"));

		// "Available languages" menu items
		final ButtonGroup group5 = new ButtonGroup();
		JRadioButtonMenuItem autoChck = null;
		for (Language language : LanguageManager.languages) {
			File flagFile = MToolKit.getFile(IdConst.FLAGS_DIR + language.getKey()
					+ IdConst.TYPE_PIC);
			boolean isDefault = language.getKey().equalsIgnoreCase(
					LanguageManager.getLanguage().getKey());
			JRadioButtonMenuItem itemChck = null;
			if (flagFile == null) {
				itemChck = new JRadioButtonMenuItem(language.getName(), isDefault);
			} else {
				itemChck = new JRadioButtonMenuItem(language.getName(), new ImageIcon(
						flagFile.getAbsolutePath()), isDefault);

			}
			itemChck.addActionListener(langListener);
			itemChck.setActionCommand(language.getKey());
			if ("auto".equals(language.getKey())) {
				// Add the "auto" language detection
				itemChck.setToolTipText("<html><b>"
						+ LanguageManager.getString("menu_lf_language.auto.tooltip", Locale
								.getDefault().getLanguage()));
				autoChck = itemChck;
			} else {
				itemChck.setToolTipText("<html><b>"
						+ LanguageManager.getString("author") + "</b> : "
						+ language.getAuthor() + "<br><b>"
						+ LanguageManager.getString("contact") + "</b> : "
						+ language.getMoreInfo() + "<br><b>"
						+ LanguageManager.getString("version") + "</b> : "
						+ language.getVersion());
				languageMenu.add(itemChck);
			}
			group5.add(itemChck);
		}
		languageMenu.add(autoChck);
		languageMenu.add(new JSeparator());

		// "More language" menu item
		languageMenu.add(UIHelper.buildMenu("menu_lf_language.more", langListener));
		lookNFeelMenu.add(languageMenu);
		magicMenu.add(lookNFeelMenu);

		// "Auto mana" option
		autoManaMenu = new JCheckBoxMenuItem(LanguageManager.getString("automana"),
				UIHelper.getIcon("automana.gif"));
		autoManaMenu.setMnemonic('m');
		autoManaMenu.setToolTipText("<html>"
				+ LanguageManager.getString("automana.tooltip"));
		autoManaMenu.addActionListener(this);
		optionMenu.add(autoManaMenu);

		// "Auto play" option
		autoPlayMenu = new JCheckBoxMenuItem(LanguageManager
				.getString("autoaction"), UIHelper.getIcon("fast.gif"));
		autoPlayMenu.setMnemonic('p');
		autoPlayMenu.setSelected(true);
		autoPlayMenu.setToolTipText("<html>"
				+ LanguageManager.getString("autoaction.tooltip") + "<br><br>"
				+ MagicUIComponents.HTML_ICON_TIP
				+ LanguageManager.getString("yourtbzTTtip2"));
		autoPlayMenu.addActionListener(this);
		optionMenu.add(autoPlayMenu);

		JMenuItem settingsItem = UIHelper.buildMenu("menu_options_settings", this);
		settingsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0));
		optionMenu.add(settingsItem);

		initAbstractMenu();
		magicMenu.add(optionMenu);

		// "Tool" menu
		final JMenu toolMenu = UIHelper.buildMenu("tools", 't');
		toolMenu.add(UIHelper.buildMenu("menu_tools_log", this));
		toolMenu.add(UIHelper.buildMenu("menu_tools_bugreport", this));
		toolMenu.add(UIHelper.buildMenu("menu_tools_featurerequest", this));
		toolMenu.add(new JSeparator());
		toolMenu.add(UIHelper.buildMenu("menu_tools_jdb", 'd', this));
		JMenuItem cardBuilderMenu = UIHelper.buildMenu("menu_tools_jcb", 'c', this);
		cardBuilderMenu.setEnabled(false);
		toolMenu.add(cardBuilderMenu);
		magicMenu.add(toolMenu);

		// "Help" menu
		JMenu questionMenu = UIHelper.buildMenu("menu_help", 'h');
		JMenuItem helpMenu = UIHelper.buildMenu("menu_help_help", 'h', this);
		helpMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		questionMenu.add(helpMenu);

		// "About M-P" menu item
		JMenuItem aboutMenu = UIHelper.buildMenu("menu_help_about", 'a', this);
		aboutMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1,
				InputEvent.SHIFT_MASK));
		questionMenu.add(aboutMenu);

		// "About TBS" menu item
		JMenuItem aboutMdbMenu = UIHelper.buildMenu("menu_help_about.tbs", 'm',
				null, this);
		aboutMdbMenu.setIcon(aboutMenu.getIcon());
		aboutMdbMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1,
				InputEvent.ALT_MASK));
		questionMenu.add(aboutMdbMenu);
		questionMenu.add(new JSeparator());

		// "Check for update" menu item
		final JMenuItem checkUpdateMenu = UIHelper.buildMenu(
				"menu_help_check-update", 'u', this);
		checkUpdateMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F10,
				InputEvent.SHIFT_MASK));
		questionMenu.add(checkUpdateMenu);
		questionMenu.add(UIHelper.buildMenu("menu_help_mailing", 'l', this));
		magicMenu.add(questionMenu);

		// filler
		final JMenu disabledMenu = new JMenu();
		disabledMenu.setEnabled(false);
		disabledMenu
				.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
		magicMenu.add(disabledMenu);

		// "Version" disabled menu item
		final JMenu menu = new JMenu(LanguageManager.getString("version")
				+ IdConst.VERSION);
		menu.setEnabled(false);
		// magicMenu.add(Box.createHorizontalGlue());
		magicMenu.add(menu);
		setJMenuBar(magicMenu);

		// initialize the event manager and the spell stack
		EventManager.init();
		TargetFactory.initSettings();
		CardFactory.initSettings();
		PopupManager.init();

		// The table top
		getContentPane().setLayout(new BorderLayout2());
		getContentPane().add(infoPanel, BorderLayout.SOUTH);
		TableTop.init(playerTabbedPanel, previewTabbedPanel);
		chatPanel.getRootPane().setDefaultButton(sendButton);
	}

	/**
	 * An ActionListener that listens to the radio buttons menus
	 */
	protected static class UIListener implements ActionListener {

		/**
		 * Creates a new instance of UIListener <br>
		 */
		public UIListener() {
			super();
		}

		public void actionPerformed(ActionEvent e) {
			SkinLF.installLookAndFeel(e.getActionCommand(), e.getSource());
			ZoneManager.updateLookAndFeel();
		}
	}

	/**
	 * Set the current UI as Metal
	 */
	protected static void setDefaultUI() {
		lookAndFeelName = MUIManager.LF_METAL_CLASSNAME;
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
		frameDecorated = true;
	}

	/**
	 * Set the current TBS name. Calling this method cause the mana symbols to be
	 * downloaded if it's not yet done.
	 * 
	 * @param tbsName
	 *          the TBS to define as current.
	 */
	@Override
	public final void setMdb(String tbsName) {
		super.setMdb(tbsName);
		MagicUIComponents.previewPanel.removeAll();
		Mana.init(tbsName);
		Picture pic = CardFactory.initPreview();
		MagicUIComponents.previewPanel.add(pic);
		MagicUIComponents.previewPanel.setPreferredSize(new Dimension(pic
				.getPreferredSize().width + 2, pic.getPreferredSize().height + 2));
		chosenCostPanel.initialize();
	}

	/**
	 * An ActionListener that listens to the radio buttons menus
	 */
	private final ActionListener langListener = new ActionListener() {
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			final String command = e.getActionCommand();
			if ("menu_lf_language.more".equals(command)) {
				// goto "more language" page
				try {
					WebBrowser
							.launchBrowser("http://sourceforge.net/project/showfiles.php?group_id="
									+ IdConst.PROJECT_ID + "&package_id=107882");
				} catch (Exception e1) {
					JOptionPane.showOptionDialog(magicForm, LanguageManager
							.getString("error"), LanguageManager.getString("web-pb"),
							JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE, UIHelper
									.getIcon("wiz_update_error.gif"), null, null);
				}
				return;
			}
			LanguageManager.setLanguage(e.getActionCommand());
			JOptionPane
					.showMessageDialog(MagicUIComponents.magicForm, LanguageManager
							.getString("menu_lf_language.restart"), LanguageManager
							.getString("restart"), JOptionPane.INFORMATION_MESSAGE);
		}
	};

	/**
	 * Indicates the UI is loaded.
	 * 
	 * @return <code>true</code> if the UI is loaded.
	 */
	public static boolean isUILoaded() {
		return magicForm != null;
	}

	/**
	 * all radioButtonMenu of each L&F available
	 */
	protected JRadioButtonMenuItem[] lookAndFeels;

	/**
	 * Comment for <code>lookAndFeelName</code>
	 */
	public static String lookAndFeelName;

	/**
	 * Comment for <code>magicForm</code>
	 */
	public static Magic magicForm;

	/**
	 * Comment for <code>frameDecorated</code>
	 */
	public static boolean frameDecorated;

	/**
	 * Comment for <code>autoManaMenu</code>
	 */
	public static JCheckBoxMenuItem autoManaMenu;

	/**
	 * Comment for <code>autoPlayMenu</code>
	 */
	public static JCheckBoxMenuItem autoPlayMenu;

	/**
	 * Comment for <code>chatPanel</code>
	 */
	public static JPanel chatPanel;

	/**
	 * Comment for <code>historyText</code>
	 */
	public static EditorPane chatHistoryText;

	/**
	 * Comment for <code>themeMenu</code>
	 */
	public static JMenu themeMenu;

	/**
	 * Comment for <code>previewPanel</code>
	 */
	public static JPanel previewPanel;

	/**
	 * Comment for <code>sendButton</code>
	 */
	public static JButton sendButton;

	/**
	 * Comment for <code>sendTxt</code>
	 */
	public static JTextField sendTxt;

	/**
	 * The button used to skip/decline to response to the current ability.
	 */
	public static JButton skipButton;

	/**
	 * Comment for <code>skipMenu</code>
	 */
	public static JMenuItem skipMenu;

	/**
	 * The label containing information about the active player
	 */
	public static JLabel waitingLabel;

	/**
	 * 
	 */
	protected static JMenuItem reverseArtCheck;

	/**
	 * 
	 */
	protected static JMenuItem reverseSideCheck;

	/**
	 * 
	 */
	protected static JCheckBoxMenuItem soundMenu;

	/**
	 * 
	 */
	protected static JMenuItem initialdelayMenu;

	/**
	 * 
	 */
	protected static JMenuItem dismissdelayMenu;

	private static JMenu languageMenu;

	/**
	 * 
	 */
	protected static JMenuItem moreThemeMenu;

	/**
	 * The main panel containing player stuffs.
	 */
	public static JTabbedPane playerTabbedPanel;

	/**
	 * The logging editor pane.
	 */
	public static EditorPane logListing;

	/**
	 * Is the main avatar picture is separated from the tabbed pane containing
	 * zones.
	 */
	public static boolean speparateAvatar = true;

	/**
	 * The splash screen
	 */
	protected static SplashScreen splash;

	/**
	 * 
	 */
	protected static JMenu cardBorderMenu;

	/**
	 * The chosen cost panel
	 */
	public static ChosenCostPanel chosenCostPanel;

	/**
	 * The tip picture.
	 */
	public static final String HTML_ICON_TIP = "<img src='file:///"
			+ MToolKit.getIconPath("tip.gif") + "'>&nbsp;";

	/**
	 * The warning picture.
	 */
	public static final String HTML_ICON_WARNING = "<br><img src='file:///"
			+ MToolKit.getIconPath("warn.gif") + "'>&nbsp;";

	/**
	 * The button restoring the current backgrounded wizard.
	 */
	public static MessageButton backgroundBtn;

	/**
	 * Data base panel. Displayed in the <code>previewTabbedPanel</code>.
	 */
	public static CardPropertiesPanel databasePanel;

	/**
	 * The turn label
	 */
	public JLabel turnsLbl;

	/**
	 * The moreInfo label
	 */
	public JLabel moreInfoLbl;
}