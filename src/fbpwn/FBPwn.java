/*
 * FBPwn
 * 
 * http://sourceforge.net/projects/fbpwn/
 * 
 * Copyright (C) 2011 - FBPwn
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fbpwn;

import fbpwn.core.FacebookManager;
import fbpwn.ui.MainForm;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.pushingpixels.substance.api.skin.SubstanceNebulaLookAndFeel;

/**
 * Main class for FBPwn 
 */
public class FBPwn {

    public static final String appVersion = "Beta - 0.1.5";

    /**
     * Runs the application using the default Swing GUI
     * @param args command line arguments
     */
    public static void main(String[] args) {
        try {
            FacebookManager.getInstance();

            SwingUtilities.invokeAndWait(new Runnable() {

                @Override
                public void run() {
                    try {
                        UIManager.setLookAndFeel(new SubstanceNebulaLookAndFeel());
                        JFrame.setDefaultLookAndFeelDecorated(true);
                        JDialog.setDefaultLookAndFeelDecorated(true);


                        MainForm mainForm = new MainForm();
                        FacebookManager.getInstance().setFacebookGUI(mainForm);
                        mainForm.setVisible(true);

                    } catch (UnsupportedLookAndFeelException ex) {
                        Logger.getLogger(FBPwn.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
        } catch (InterruptedException ex) {
            Logger.getLogger(FBPwn.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(FBPwn.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String getUpdates() throws MalformedURLException, IOException {
        URL changeLog = new URL("http://fbpwn.googlecode.com/svn/wiki/ChangeLog.wiki");
        URLConnection connection = changeLog.openConnection();
        BufferedReader in =
                new BufferedReader(new InputStreamReader(connection.getInputStream()));


        String line;
        String page = "";

        while ((line = in.readLine()) != null) {
            if(line.startsWith("#")) {
                continue;
            }
            page += line + "\n";
        }
        in.close();
        return page;
    }
}
