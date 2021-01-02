package PCB;
import java.util.Scanner;
/*
" ____                                __  __                                   \r\n"+
"|  _ \ _ __ ___   ___ ___  ___ ___  |  \/  | __ _ _ __   __ _  __ _  ___ _ __ \r\n"+
"| |_) | '__/ _ \ / __/ _ \/ __/ __| | |\/| |/ _` | '_ \ / _` |/ _` |/ _ \ '__|\r\n"+
"|  __/| | | (_) | (_|  __/\__ \__ \ | |  | | (_| | | | | (_| | (_| |  __/ |   \r\n"+
"|_|   |_|  \___/ \___\___||___/___/ |_|  |_|\__,_|_| |_|\__,_|\__, |\___|_|   \r\n"+
"                                                              |___/           \r\n"+
 */
public class Main {
    private static Scanner scanner =new Scanner(System.in);
    private static String command=null;
    static processManage processManagerRun=new processManage();
    static Boolean quit_flag = true;  // 关闭按钮 false = 退出
    
    public static void main(String[] args)
    {
    	System.out.println(" ____  ____   __    ___  ____  ____  ____ \r\n" + 
    			"(  _ \\(  _ \\ /  \\  / __)(  __)/ ___)/ ___)\r\n" + 
    			" ) __/ )   /(  O )( (__  ) _) \\___ \\\\___ \\\r\n" + 
    			"(__)  (__\\_) \\__/  \\___)(____)(____/(____/ \r\n" + 
    			" _  _   __   __ _   __    ___  ____  ____ \r\n" + 
    			"( \\/ ) / _\\ (  ( \\ / _\\  / __)(  __)(  _ \\\r\n" + 
    			"/ \\/ \\/    \\/    //    \\( (_ \\ ) _)  )   /\r\n" + 
    			"\\_)(_/\\_/\\_/\\_)__)\\_/\\_/ \\___/(____)(__\\_)");
    	System.out.println("    ____  _  _        ____   __    __  \r\n" + 
    			"   (  _ \\( \\/ )      (  _ \\ / _\\  /  \\ \r\n" + 
    			"    ) _ ( )  /        ) __//    \\(  O )\r\n" + 
    			"   (____/(__/        (__)  \\_/\\_/ \\__/ ");
    	initCmd();
    	processManagerRun.createResources();
    	//ShowCmdHelp();
        while(quit_flag!=false){
            
            System.out.print(">> ");
            command=null;
            command=scanner.nextLine();
            command=command.trim();
            exeCmd(command);
        }
    	
    }
    private static void initCmd()
    {
    	int illegalShow=0;
    	illegalShow=processManagerRun.createProcess("init", 0);
    	if(illegalShow==1)
    	{
    		System.out.println("init process Created Successed!");
    	}
    	else 
    	{
    		System.out.println("init process Created Default!");
    	}
    }
    private static void exeCmd(String command)
    {
    	String cmds[]=command.split(" ");
    	int key;
		int requestNum = 0;
		switch(cmds[0])
		{
		case "cr":
			if(cmds.length!=3) {
				System.out.println("error: cr <pName> <priority>");
				return;
			}
			if(Integer.valueOf(cmds[2])!=1&&Integer.valueOf(cmds[2])!=2)
			{
				System.out.println("error: cr <pName> <priority>(=1 or 2)");
				return;
			}
			key=processManagerRun.createProcess(cmds[1],Integer.valueOf(cmds[2]));
			if(key==1) 
			{
				System.out.println("[success]进程(name:"+cmds[1] +")创建成功! ");
				return;
			}
		case "de":
			if(cmds.length!=2)
			{
				System.out.println("error: de [pName]");
				return;
			}
			key =processManagerRun.destoryProcess(cmds[1]);
			if(key ==1)
			{
				System.out.println("[success]进程(name:" +cmds[1]+ ")撤销成功!");
			}
			else
				System.out.println("error");
			return;
		case "req":


			switch (cmds.length)
			{
			case 2:  // default number 默认number = 1
				key = processManagerRun.requestResources(cmds[1], 1);
				break;
			case 3:  // req R1 2
				requestNum = Integer.valueOf(cmds[2]);
				key = processManagerRun.requestResources(cmds[1], requestNum);
				break;
			default:
				System.out.println("error:req <r-name> <number>");
				return ;
			}

			// 1 - 合法
			// 2 - 请求资源不存在
			// 3 - 请求超过此资源总量
			switch (key)
			{
			case 1:
				System.out.println ("[success]资源 "+cmds[1] + " 请求成功!");
				break;

			case 2:
				System.out.println("error:请求资源不存在!");
				break;

			case 3:
				System.out.println("error:请求超过此资源总量!");
				break;

			default:
				break;
			}
			return;
		case "rel":
			switch (cmds.length)
			{
			case 2:  // rel [r-name] 缺省number 默认 number = 1

				key = processManagerRun.releaseResources(cmds[1], 1);
				break;

			case 3:  // rel [r-name] [number]
				requestNum = Integer.valueOf(cmds[2]); 
				key = processManagerRun.releaseResources(cmds[1], requestNum);
				break;

			default:
				System.out.println("error:rel 命令不合法!" );
				return;
			}

			// 1 - 合法
			// 2 - 释放资源不存在
			// 3 - 释放超过此资源总量
			switch (key)
			{
			case 1:
				System.out.println("[success]资源 "+ cmds[1]+ " 释放成功!" );
				break;

			case 2:
				System.out.println("error:释放资源不存在!" );
				break;

			case 3:
				System.out.println( "error:释放超过此资源总量!" );
				break;

			case 4:
				System.out.println("error:释放资源量无效!" );
				break;

			case 5:
				System.out.println("error:该进程无此资源可释放!");
				break;

			default:
				break;
			}
			return ;
		case "rq":
			if(cmds.length==1)
			{
				processManagerRun.showReadyList();
			}
			else
				System.out.print("error: rq");
			return;
		case "rt":
			if(cmds.length==1)
			{
				processManagerRun.showResourcessTable();
			}
			else
				System.out.print("error: rt");
			return ;
		case "pt":
			if(cmds.length==1)
			{
				processManagerRun.showProcessTable();
			}
			else
				System.out.print("error: ps");
			return;
		case "to":
			if(cmds.length==1)
			{
				processManagerRun.Schedule();
			}
			else
				System.out.print("error: to");
			return ;
		case "h":
			ShowCmdHelp();
			return;
		case "q":
			System.out.println("Goodbye!");
			quit_flag=false;
			return ;
		default:
			System.out.println("非法指令！");
			break;
		}
    }
    private static void ShowCmdHelp()
    {
    	System.out.println("___________________________________________________________");
    	System.out.println("cr\tCreate Process eg:cr <name> <priority>(=1 or 2)");
    	System.out.println("de\tDelete Process eg:de <pName> " );
    	System.out.println("req\tRequest Resource eg:req <resource name> <# of units>");
    	System.out.println("rel\tRelease Resource eg:rel <resource name> <# of units>");
    	System.out.println("rq\tShow Ready Queue");
    	System.out.println("rt\tShow Resource Table" );
    	System.out.println("pt\tShow Process Table" );
    	System.out.println("to\tTime Out" );
    	System.out.println("q\tQuit");
    	System.out.println("h\tHelp");
    	System.out.println("___________________________________________________________");
    }
}
