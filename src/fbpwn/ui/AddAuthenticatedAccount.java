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
package fbpwn.ui;

import fbpwn.core.AuthenticatedAccount;
import fbpwn.core.ExceptionHandler;
import fbpwn.core.FacebookException;

import fbpwn.core.FacebookManager;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/**
 * A dialog asks for account details, and tries to login using these details 
 */
public class AddAuthenticatedAccount extends javax.swing.JDialog {

    private AuthenticatedAccount authenticatedAccount = null;

    /** Creates new form AddAuthenticatedAccount
     * @param parent parent for this dialog
     * @param modal modal or not
     */
    public AddAuthenticatedAccount(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((int) (dim.getWidth() / 2 - getWidth() / 2), (int) (dim.getHeight() / 2 - getHeight() / 2));

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Add new authenticated account");

        jLabel1.setText("Username:");

        jLabel2.setText("Password:");

        jTextField1.setNextFocusableComponent(jTextField2);

        jTextField2.setNextFocusableComponent(jButton1);

        jButton1.setText("Login");
        jButton1.setNextFocusableComponent(jButton2);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Cancel");
        jButton2.setNextFocusableComponent(jTextField1);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextField2)
                    .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE))
                .addGap(38, 38, 38)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

    authenticatedAccount = null;
    dispose();
}//GEN-LAST:event_jButton2ActionPerformed

private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    AuthenticatedAccount authenticatedProfile = null;

    final LoadingDialog loadingDialog =
            new LoadingDialog(null, true, "Attempting to login ...");

    SwingWorker backgrn = new SwingWorker() {

        @Override
        protected void done() {
            super.done();
            loadingDialog.dispose();
        }

        @Override
        protected Object doInBackground() throws Exception {

            try {                
                return FacebookManager.getInstance().logIn(jTextField1.getText(),
                        jTextField2.getText());
            } catch (final FacebookException exception) {
                SwingUtilities.invokeAndWait(new Runnable() {

                    @Override
                    public void run() {
                        FacebookExceptionDisplayer.displayException(exception);
                    }
                });
                Logger.getLogger(AddAuthenticatedAccount.class.getName()).log(Level.SEVERE, "Exception in thread: " + Thread.currentThread().getName(), exception);
                return null;
            } catch (final IOException ex) {
                SwingUtilities.invokeAndWait(new Runnable() {

                    @Override
                    public void run() {
                        JOptionPane.showMessageDialog(null, "Failed to connect to server", "Error Occurred", JOptionPane.ERROR_MESSAGE);
                    }
                });
                Logger.getLogger(AddAuthenticatedAccount.class.getName()).log(Level.SEVERE, "Exception in thread: " + Thread.currentThread().getName(), ex);
                return null;
            }

        }
    };
    backgrn.execute();
    loadingDialog.setLocationRelativeTo(null);
    loadingDialog.setVisible(true);
    try {
        authenticatedProfile = (AuthenticatedAccount) backgrn.get();
    } catch (InterruptedException ex) {
        Logger.getLogger(AddAuthenticatedAccount.class.getName()).log(Level.SEVERE, "Exception in thread: " + Thread.currentThread().getName(), ex);
    } catch (ExecutionException ex) {
        Logger.getLogger(AddAuthenticatedAccount.class.getName()).log(Level.SEVERE, "Exception in thread: " + Thread.currentThread().getName(), ex);
    }

    this.authenticatedAccount = authenticatedProfile;

    dispose();
}//GEN-LAST:event_jButton1ActionPerformed
    /**
     * Shows a dialog to ask for details for an authenticated account.
     *   * The dialog also tries to login to this authenticated account
     * @return The authenticated account after logging in
     */
    public AuthenticatedAccount showAddAuthenticatedAccountDialog() {
        setVisible(true);
        return authenticatedAccount;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
}
