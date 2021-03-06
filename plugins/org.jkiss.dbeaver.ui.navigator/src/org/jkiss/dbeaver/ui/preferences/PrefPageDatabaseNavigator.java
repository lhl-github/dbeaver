/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2019 Serge Rider (serge@jkiss.org)
 * Copyright (C) 2011-2012 Eugene Fradkin (eugene.fradkin@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jkiss.dbeaver.ui.preferences;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.ModelPreferences;
import org.jkiss.dbeaver.ui.internal.UINavigatorMessages;
import org.jkiss.dbeaver.model.preferences.DBPPreferenceStore;
import org.jkiss.dbeaver.runtime.DBWorkbench;
import org.jkiss.dbeaver.ui.UIUtils;
import org.jkiss.dbeaver.ui.navigator.NavigatorPreferences;
import org.jkiss.dbeaver.ui.navigator.database.NavigatorViewBase;
import org.jkiss.dbeaver.utils.PrefUtils;
import org.jkiss.utils.CommonUtils;

import java.util.Locale;

/**
 * PrefPageDatabaseNavigator
 */
public class PrefPageDatabaseNavigator extends AbstractPrefPage implements IWorkbenchPreferencePage, IWorkbenchPropertyPage
{
    public static final String PAGE_ID = "org.jkiss.dbeaver.preferences.navigator"; //$NON-NLS-1$

    private Button expandOnConnectCheck;
    private Button showObjectTipsCheck;
    private Button sortCaseInsensitiveCheck;
    private Button sortFoldersFirstCheck;
    private Button colorAllNodesCheck;
    private Button showResourceFolderPlaceholdersCheck;
    private Button groupByDriverCheck;
    private Text longListFetchSizeText;
    private Combo dsDoubleClickBehavior;
    private Combo objDoubleClickBehavior;

    public PrefPageDatabaseNavigator()
    {
        super();
        setPreferenceStore(new PreferenceStoreDelegate(DBWorkbench.getPlatform().getPreferenceStore()));
    }

    @Override
    public void init(IWorkbench workbench)
    {

    }

    @Override
    protected Control createContents(Composite parent)
    {
        Composite composite = UIUtils.createPlaceholder(parent, 1, 5);

        {
            Group navigatorGroup = UIUtils.createControlGroup(composite, UINavigatorMessages.pref_page_database_general_group_navigator, 2, SWT.NONE, 0);

            expandOnConnectCheck = UIUtils.createCheckbox(navigatorGroup, UINavigatorMessages.pref_page_database_general_label_expand_navigator_tree, "", false, 2);
            showObjectTipsCheck = UIUtils.createCheckbox(navigatorGroup, UINavigatorMessages.pref_page_database_general_label_show_tips_in_tree, UINavigatorMessages.pref_page_database_general_label_show_tips_in_tree_tip, false, 2);
            sortCaseInsensitiveCheck = UIUtils.createCheckbox(navigatorGroup, UINavigatorMessages.pref_page_database_general_label_order_elements_alphabetically, "", false, 2);

            sortFoldersFirstCheck = UIUtils.createCheckbox(navigatorGroup, UINavigatorMessages.pref_page_database_general_label_folders_first, UINavigatorMessages.pref_page_database_general_label_folders_first_tip, false, 2);
            showResourceFolderPlaceholdersCheck = UIUtils.createCheckbox(navigatorGroup, UINavigatorMessages.pref_page_database_general_label_show_folder_placeholders, UINavigatorMessages.pref_page_database_general_label_show_folder_placeholders_tip, false, 2);
            groupByDriverCheck = UIUtils.createCheckbox(navigatorGroup, UINavigatorMessages.pref_page_database_general_label_group_database_by_driver, "", false, 2);
            groupByDriverCheck.setEnabled(false);

            colorAllNodesCheck = UIUtils.createCheckbox(navigatorGroup, UINavigatorMessages.pref_page_database_general_label_color_all_nodes, UINavigatorMessages.pref_page_database_general_label_color_all_nodes_tip, false, 2);

            longListFetchSizeText = UIUtils.createLabelText(navigatorGroup, UINavigatorMessages.pref_page_database_general_label_long_list_fetch_size, "", SWT.BORDER);
            longListFetchSizeText.setToolTipText(UINavigatorMessages.pref_page_database_general_label_long_list_fetch_size_tip);
            longListFetchSizeText.addVerifyListener(UIUtils.getIntegerVerifyListener(Locale.getDefault()));

            objDoubleClickBehavior = UIUtils.createLabelCombo(navigatorGroup, UINavigatorMessages.pref_page_database_general_label_double_click_node, SWT.DROP_DOWN | SWT.READ_ONLY);
            objDoubleClickBehavior.add(UINavigatorMessages.pref_page_database_general_label_double_click_node_open_properties, 0);
            objDoubleClickBehavior.add(UINavigatorMessages.pref_page_database_general_label_double_click_node_expand_collapse, 1);

            dsDoubleClickBehavior = UIUtils.createLabelCombo(navigatorGroup, UINavigatorMessages.pref_page_database_general_label_double_click_connection, SWT.DROP_DOWN | SWT.READ_ONLY);
            dsDoubleClickBehavior.add(UINavigatorMessages.pref_page_database_general_label_double_click_connection_open_properties, NavigatorViewBase.DoubleClickBehavior.EDIT.ordinal());
            dsDoubleClickBehavior.add(UINavigatorMessages.pref_page_database_general_label_double_click_connection_conn_disconn, NavigatorViewBase.DoubleClickBehavior.CONNECT.ordinal());
            dsDoubleClickBehavior.add(UINavigatorMessages.pref_page_database_general_label_double_click_connection_open_sqleditor, NavigatorViewBase.DoubleClickBehavior.SQL_EDITOR.ordinal());
            dsDoubleClickBehavior.add(UINavigatorMessages.pref_page_database_general_label_double_click_connection_expand_collapse, NavigatorViewBase.DoubleClickBehavior.EXPAND.ordinal());
            dsDoubleClickBehavior.add(UINavigatorMessages.pref_page_database_general_label_double_click_connection_open_new_sqleditor, NavigatorViewBase.DoubleClickBehavior.SQL_EDITOR_NEW.ordinal());
        }

        performDefaults();

        return composite;
    }

    @Override
    protected void performDefaults()
    {
        DBPPreferenceStore store = DBWorkbench.getPlatform().getPreferenceStore();

        expandOnConnectCheck.setSelection(store.getBoolean(NavigatorPreferences.NAVIGATOR_EXPAND_ON_CONNECT));
        showObjectTipsCheck.setSelection(store.getBoolean(NavigatorPreferences.NAVIGATOR_SHOW_OBJECT_TIPS));
        sortCaseInsensitiveCheck.setSelection(store.getBoolean(ModelPreferences.NAVIGATOR_SORT_ALPHABETICALLY));
        sortFoldersFirstCheck.setSelection(store.getBoolean(ModelPreferences.NAVIGATOR_SORT_FOLDERS_FIRST));
        colorAllNodesCheck.setSelection(store.getBoolean(NavigatorPreferences.NAVIGATOR_COLOR_ALL_NODES));
        showResourceFolderPlaceholdersCheck.setSelection(store.getBoolean(ModelPreferences.NAVIGATOR_SHOW_FOLDER_PLACEHOLDERS));
        groupByDriverCheck.setSelection(store.getBoolean(NavigatorPreferences.NAVIGATOR_GROUP_BY_DRIVER));
        longListFetchSizeText.setText(store.getString(NavigatorPreferences.NAVIGATOR_LONG_LIST_FETCH_SIZE));
        NavigatorViewBase.DoubleClickBehavior objDCB = NavigatorViewBase.DoubleClickBehavior.valueOf(store.getString(NavigatorPreferences.NAVIGATOR_OBJECT_DOUBLE_CLICK));
        objDoubleClickBehavior.select(objDCB == NavigatorViewBase.DoubleClickBehavior.EXPAND ? 1 : 0);
        dsDoubleClickBehavior.select(
            NavigatorViewBase.DoubleClickBehavior.valueOf(store.getString(NavigatorPreferences.NAVIGATOR_CONNECTION_DOUBLE_CLICK)).ordinal());
    }

    @Override
    public boolean performOk()
    {
        DBPPreferenceStore store = DBWorkbench.getPlatform().getPreferenceStore();

        store.setValue(NavigatorPreferences.NAVIGATOR_EXPAND_ON_CONNECT, expandOnConnectCheck.getSelection());
        store.setValue(NavigatorPreferences.NAVIGATOR_SHOW_OBJECT_TIPS, showObjectTipsCheck.getSelection());
        store.setValue(ModelPreferences.NAVIGATOR_SORT_ALPHABETICALLY, sortCaseInsensitiveCheck.getSelection());
        store.setValue(ModelPreferences.NAVIGATOR_SORT_FOLDERS_FIRST, sortFoldersFirstCheck.getSelection());
        store.setValue(NavigatorPreferences.NAVIGATOR_COLOR_ALL_NODES, colorAllNodesCheck.getSelection());
        store.setValue(ModelPreferences.NAVIGATOR_SHOW_FOLDER_PLACEHOLDERS, showResourceFolderPlaceholdersCheck.getSelection());
        store.setValue(NavigatorPreferences.NAVIGATOR_GROUP_BY_DRIVER, groupByDriverCheck.getSelection());
        store.setValue(NavigatorPreferences.NAVIGATOR_LONG_LIST_FETCH_SIZE, longListFetchSizeText.getText());
        NavigatorViewBase.DoubleClickBehavior objDCB = NavigatorViewBase.DoubleClickBehavior.EXPAND;
        if (objDoubleClickBehavior.getSelectionIndex() == 0) {
            objDCB = NavigatorViewBase.DoubleClickBehavior.EDIT;
        }
        store.setValue(NavigatorPreferences.NAVIGATOR_OBJECT_DOUBLE_CLICK, objDCB.name());
        store.setValue(NavigatorPreferences.NAVIGATOR_CONNECTION_DOUBLE_CLICK,
            CommonUtils.fromOrdinal(NavigatorViewBase.DoubleClickBehavior.class, dsDoubleClickBehavior.getSelectionIndex()).name());

        PrefUtils.savePreferenceStore(store);

        return true;
    }

    @Override
    public void applyData(Object data)
    {
        super.applyData(data);
    }

    @Nullable
    @Override
    public IAdaptable getElement()
    {
        return null;
    }

    @Override
    public void setElement(IAdaptable element)
    {
    }

}