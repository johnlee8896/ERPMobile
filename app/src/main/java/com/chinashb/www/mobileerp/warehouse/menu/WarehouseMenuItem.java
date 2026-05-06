package com.chinashb.www.mobileerp.warehouse.menu;

/***
 * @date 创建时间 2026/5/2 15:06
 * @author 作者: code-x John
 * @description 仓库主页菜单项
 */
public class WarehouseMenuItem {

    private final String moduleCode;
    private final String menuCode;
    private final String title;
    private final int iconRes;
    private final int sortNo;
    private final boolean requiresLogin;

    public WarehouseMenuItem(String moduleCode, String menuCode, String title, int iconRes, int sortNo, boolean requiresLogin) {
        this.moduleCode = moduleCode;
        this.menuCode = menuCode;
        this.title = title;
        this.iconRes = iconRes;
        this.sortNo = sortNo;
        this.requiresLogin = requiresLogin;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public String getMenuCode() {
        return menuCode;
    }

    public String getTitle() {
        return title;
    }

    public int getIconRes() {
        return iconRes;
    }

    public int getSortNo() {
        return sortNo;
    }

    public boolean isRequiresLogin() {
        return requiresLogin;
    }
}
