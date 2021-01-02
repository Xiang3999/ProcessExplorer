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
    	System.out.println("******************************************");
    	System.out.println("***********Process  Manager************");
    	System.out.println("******************************************");
    	initCmd();
    	processManagerRun.createResources();
    	ShowCmdHelp();
    	
    	System.out.print(">> ");
        command=scanner.nextLine();
        command=command.trim();//删除空白开头
        while(quit_flag!=false){
            exeCmd(command);
            System.out.print(">> ");
            command=null;
            command=scanner.nextLine();
            command=command.trim();
        }
    	
    }
    private static void initCmd()
    {
    	int illegalShow=0;
    	illegalShow=processManagerRun.createProcess("init", 0);
    	if(illegalShow==1)
    	{
    		System.out.println("init 进程创建成功 !");
    	}
    	else 
    	{
    		System.out.println("init 进程创建失败 !");
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
				System.out.println("error: cr [pName] [priority]");
				return;
			}
			if(Integer.valueOf(cmds[2])!=1&&Integer.valueOf(cmds[2])!=2)
			{
				System.out.println("error: cr [pName] [priority=0  or 1]");
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
				System.out.println("error:req [r-name] [number]");
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
		case "sready":
			if(cmds.length==1)
			{
				processManagerRun.showReadyList();
			}
			else
				System.out.print("error: sready");
			return;
		case "sres":
			if(cmds.length==1)
			{
				processManagerRun.showResourcessTable();
			}
			else
				System.out.print("error: sres");
			return ;
		case "ps":
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
				System.out.print("error: ps");
			return ;
		case "quit":
			System.out.println("QUIT");
			quit_flag=false;
			return ;
		default:
			break;
		}
    }
    private static void ShowCmdHelp()
    {
    	System.out.println("------------------- Help ----------------------");
    	System.out.println("创建进程:     cr [pName] [priority] 如:cr x 1");
    	System.out.println("撤销进程:     de [pName]            如: de x " );
    	System.out.println("请求资源:     req [r-name] [number] 如: req R1 2");
    	System.out.println("释放资源:     rel [r-name] [number] 如: rel R1 2");
    	System.out.println("显示就绪队列: sready");
    	System.out.println("显示资源表:   sres" );
    	System.out.println("显示进程表:   ps" );
    	System.out.println("时间片切换:   to" );
    	System.out.println("退出Cmd:      quit");
    	System.out.println("-------------------------------------------------" );
    }
}
