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
    static Boolean quit_flag = true;  // �رհ�ť false = �˳�
    
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
        command=command.trim();//ɾ���հ׿�ͷ
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
    		System.out.println("init ���̴����ɹ� !");
    	}
    	else 
    	{
    		System.out.println("init ���̴���ʧ�� !");
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
				System.out.println("[success]����(name:"+cmds[1] +")�����ɹ�! ");
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
				System.out.println("[success]����(name:" +cmds[1]+ ")�����ɹ�!");
			}
			else
				System.out.println("error");
			return;
		case "req":


			switch (cmds.length)
			{
			case 2:  // default number Ĭ��number = 1
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

			// 1 - �Ϸ�
			// 2 - ������Դ������
			// 3 - ���󳬹�����Դ����
			switch (key)
			{
			case 1:
				System.out.println ("[success]��Դ "+cmds[1] + " ����ɹ�!");
				break;

			case 2:
				System.out.println("error:������Դ������!");
				break;

			case 3:
				System.out.println("error:���󳬹�����Դ����!");
				break;

			default:
				break;
			}
			return;
		case "rel":
			switch (cmds.length)
			{
			case 2:  // rel [r-name] ȱʡnumber Ĭ�� number = 1

				key = processManagerRun.releaseResources(cmds[1], 1);
				break;

			case 3:  // rel [r-name] [number]
				requestNum = Integer.valueOf(cmds[2]); 
				key = processManagerRun.releaseResources(cmds[1], requestNum);
				break;

			default:
				System.out.println("error:rel ����Ϸ�!" );
				return;
			}

			// 1 - �Ϸ�
			// 2 - �ͷ���Դ������
			// 3 - �ͷų�������Դ����
			switch (key)
			{
			case 1:
				System.out.println("[success]��Դ "+ cmds[1]+ " �ͷųɹ�!" );
				break;

			case 2:
				System.out.println("error:�ͷ���Դ������!" );
				break;

			case 3:
				System.out.println( "error:�ͷų�������Դ����!" );
				break;

			case 4:
				System.out.println("error:�ͷ���Դ����Ч!" );
				break;

			case 5:
				System.out.println("error:�ý����޴���Դ���ͷ�!");
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
    	System.out.println("��������:     cr [pName] [priority] ��:cr x 1");
    	System.out.println("��������:     de [pName]            ��: de x " );
    	System.out.println("������Դ:     req [r-name] [number] ��: req R1 2");
    	System.out.println("�ͷ���Դ:     rel [r-name] [number] ��: rel R1 2");
    	System.out.println("��ʾ��������: sready");
    	System.out.println("��ʾ��Դ��:   sres" );
    	System.out.println("��ʾ���̱�:   ps" );
    	System.out.println("ʱ��Ƭ�л�:   to" );
    	System.out.println("�˳�Cmd:      quit");
    	System.out.println("-------------------------------------------------" );
    }
}
