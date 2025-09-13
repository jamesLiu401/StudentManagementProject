/**
 * 学生管理系统 - 主入口文件
 * Student Management System - Main Entry Point
 * 
 * 这个文件作为整个项目的入口点，负责启动 Electron 应用
 * This file serves as the main entry point for the entire project, responsible for starting the Electron application
 * 
 * @author James Liu
 * @version 1.0.0
 */

const { app, BrowserWindow, Menu, shell, dialog } = require('electron');
const isDev = require('electron-is-dev');
const path = require('path');

// 全局变量
let mainWindow;

/**
 * 创建主窗口
 * Create main window
 */
function createWindow() {
    // 创建浏览器窗口
    mainWindow = new BrowserWindow({
        width: 1200,
        height: 800,
        minWidth: 800,
        minHeight: 600,
        webPreferences: {
            nodeIntegration: false,
            contextIsolation: true,
            enableRemoteModule: false,
            webSecurity: true
        },
        icon: path.join(__dirname, 'assets/icon.png'), // 应用图标
        show: false, // 先不显示，等加载完成后再显示
        titleBarStyle: 'default',
        title: '学生管理系统 - Student Management System'
    });

    // 加载应用
    const startUrl = isDev 
        ? 'http://localhost:3001' 
        : `file://${path.join(__dirname, 'build/index.html')}`;
    
    // 在开发环境下，等待React服务器启动
    if (isDev) {
        const { exec } = require('child_process');
        
        // 检查React服务器是否运行
        const checkServer = () => {
            const http = require('http');
            const req = http.get('http://localhost:3001', (res) => {
                mainWindow.loadURL(startUrl);
            });
            
            req.on('error', (err) => {
                console.log('等待React开发服务器启动...');
                setTimeout(checkServer, 2000);
            });
        };
        
        checkServer();
    } else {
        mainWindow.loadURL(startUrl);
    }

    // 窗口准备好后显示
    mainWindow.once('ready-to-show', () => {
        mainWindow.show();
        
        // 开发环境下打开开发者工具
        if (isDev) {
            mainWindow.webContents.openDevTools();
        }
    });

    // 处理窗口关闭
    mainWindow.on('closed', () => {
        mainWindow = null;
    });

    // 处理外部链接
    mainWindow.webContents.setWindowOpenHandler(({ url }) => {
        shell.openExternal(url);
        return { action: 'deny' };
    });

    // 创建菜单
    createMenu();
}

/**
 * 创建应用菜单
 * Create application menu
 */
function createMenu() {
    const template = [
        {
            label: '文件',
            submenu: [
                {
                    label: '新建',
                    accelerator: 'CmdOrCtrl+N',
                    click: () => {
                        // 可以添加新建功能
                        dialog.showMessageBox(mainWindow, {
                            type: 'info',
                            title: '新建',
                            message: '新建功能待实现'
                        });
                    }
                },
                {
                    label: '打开',
                    accelerator: 'CmdOrCtrl+O',
                    click: () => {
                        // 可以添加打开功能
                        dialog.showMessageBox(mainWindow, {
                            type: 'info',
                            title: '打开',
                            message: '打开功能待实现'
                        });
                    }
                },
                { type: 'separator' },
                {
                    label: '退出',
                    accelerator: process.platform === 'darwin' ? 'Cmd+Q' : 'Ctrl+Q',
                    click: () => {
                        app.quit();
                    }
                }
            ]
        },
        {
            label: '编辑',
            submenu: [
                { label: '撤销', accelerator: 'CmdOrCtrl+Z', role: 'undo' },
                { label: '重做', accelerator: 'Shift+CmdOrCtrl+Z', role: 'redo' },
                { type: 'separator' },
                { label: '剪切', accelerator: 'CmdOrCtrl+X', role: 'cut' },
                { label: '复制', accelerator: 'CmdOrCtrl+C', role: 'copy' },
                { label: '粘贴', accelerator: 'CmdOrCtrl+V', role: 'paste' }
            ]
        },
        {
            label: '视图',
            submenu: [
                { label: '重新加载', accelerator: 'CmdOrCtrl+R', role: 'reload' },
                { label: '强制重新加载', accelerator: 'CmdOrCtrl+Shift+R', role: 'forceReload' },
                { label: '切换开发者工具', accelerator: process.platform === 'darwin' ? 'Alt+Cmd+I' : 'Ctrl+Shift+I', role: 'toggleDevTools' },
                { type: 'separator' },
                { label: '实际大小', accelerator: 'CmdOrCtrl+0', role: 'resetZoom' },
                { label: '放大', accelerator: 'CmdOrCtrl+Plus', role: 'zoomIn' },
                { label: '缩小', accelerator: 'CmdOrCtrl+-', role: 'zoomOut' },
                { type: 'separator' },
                { label: '切换全屏', accelerator: 'F11', role: 'togglefullscreen' }
            ]
        },
        {
            label: '窗口',
            submenu: [
                { label: '最小化', accelerator: 'CmdOrCtrl+M', role: 'minimize' },
                { label: '关闭', accelerator: 'CmdOrCtrl+W', role: 'close' }
            ]
        },
        {
            label: '帮助',
            submenu: [
                {
                    label: '关于学生管理系统',
                    click: () => {
                        // 显示关于对话框
                        dialog.showMessageBox(mainWindow, {
                            type: 'info',
                            title: '关于学生管理系统',
                            message: '学生管理系统',
                            detail: '版本: 1.0.0\n作者: James Liu\n\n这是一个基于Electron和React的学生管理系统。\n\n功能包括：\n• 学生信息管理\n• 教师信息管理\n• 缴费管理\n• 成绩管理\n• 学院专业管理\n• 班级课程管理'
                        });
                    }
                },
                {
                    label: '使用说明',
                    click: () => {
                        dialog.showMessageBox(mainWindow, {
                            type: 'info',
                            title: '使用说明',
                            message: '学生管理系统使用说明',
                            detail: '1. 首次使用请先注册管理员账户\n2. 登录后可以管理学生、教师信息\n3. 支持批量导入学生数据\n4. 可以查看和管理缴费记录\n5. 支持成绩录入和查询\n6. 可以管理学院、专业、班级信息'
                        });
                    }
                }
            ]
        }
    ];

    // macOS特殊处理
    if (process.platform === 'darwin') {
        template.unshift({
            label: app.getName(),
            submenu: [
                { label: '关于 ' + app.getName(), role: 'about' },
                { type: 'separator' },
                { label: '服务', role: 'services', submenu: [] },
                { type: 'separator' },
                { label: '隐藏 ' + app.getName(), accelerator: 'Command+H', role: 'hide' },
                { label: '隐藏其他', accelerator: 'Command+Shift+H', role: 'hideothers' },
                { label: '显示全部', role: 'unhide' },
                { type: 'separator' },
                { label: '退出', accelerator: 'Command+Q', click: () => app.quit() }
            ]
        });
    }

    const menu = Menu.buildFromTemplate(template);
    Menu.setApplicationMenu(menu);
}

/**
 * 应用启动事件
 * App ready event
 */
app.whenReady().then(() => {
    createWindow();
    
    // 在 macOS 上，当单击 dock 图标并且没有其他窗口打开时，通常会在应用中重新创建窗口
    app.on('activate', () => {
        if (BrowserWindow.getAllWindows().length === 0) {
            createWindow();
        }
    });
});

/**
 * 所有窗口关闭事件
 * All windows closed event
 */
app.on('window-all-closed', () => {
    // 在 macOS 上，应用和菜单栏通常会保持活跃状态，直到用户使用 Cmd + Q 明确退出
    if (process.platform !== 'darwin') {
        app.quit();
    }
});

/**
 * 安全设置
 * Security settings
 */
app.on('web-contents-created', (event, contents) => {
    contents.on('new-window', (event, navigationUrl) => {
        // 阻止新窗口创建，改为在默认浏览器中打开
        event.preventDefault();
        shell.openExternal(navigationUrl);
    });
});

/**
 * 处理证书错误（开发环境）
 * Handle certificate errors (development environment)
 */
if (isDev) {
    app.on('certificate-error', (event, webContents, url, error, certificate, callback) => {
        if (url.startsWith('https://localhost')) {
            // 忽略本地开发服务器的证书错误
            event.preventDefault();
            callback(true);
        } else {
            callback(false);
        }
    });
}

/**
 * 错误处理
 * Error handling
 */
process.on('uncaughtException', (error) => {
    console.error('未捕获的异常:', error);
    dialog.showErrorBox('应用程序错误', '发生了一个未预期的错误，应用程序将退出。\n\n错误详情: ' + error.message);
    app.quit();
});

process.on('unhandledRejection', (reason, promise) => {
    console.error('未处理的Promise拒绝:', reason);
});

// 导出模块（如果需要）
module.exports = {
    createWindow,
    createMenu
};
