# Process Explorer
## 目标

设计并实现一个基本的进程与资源管理器。

该管理器能够完成

- [x] 进程的控制

  - [x] 进程创建与撤销
  - [x] 进程的状态转换；
- [x] 能够基于优先级调度算法完成进程的调度

  - [x] 模拟时钟中断
  - [x] 在同优先级进程中采用时间片轮转调度算法进行调度
- [x] 能够完成资源的分配与释放，并完成进程之间的同步。
- [x] 能从用户终端读取用户命令



## 环境

硬件平台：Matebook-13(CPU：intel core i5 8th、 内存：8GB、显卡：NVIDIA Geforce MX250)

软件平台：Win10+IDE:eclipse+Java



## 系统功能需求分析

这里需要设计和实现进程与资源管理，并完成Test shell的编写，以建立系统的进程管理、调度、资源管理和分配的知识体系。

并且该管理器有如下功能：

\1. 能够完成进程的控制，如进程创建与撤销、进程的状态转换。

\2. 能够基于优先级调度算法完成进程的调度，模拟时钟中断，在同优先级进程中采用时间片轮转调度算法进行调度。

\3. 能够完成资源的分配与释放，并完成进程之间的同步。

\4. 能够读取并解释执行用户在终端输入的指令。

### 系统总体设计

​    系统总体架构如图1所示，中间部分为进程与资源管理器，属于操作系统内核的功能。该管理器具有如下功能：完成进程创建、撤销和进程调度；完成多单元 (multi_unit)资源的管理；完成资源的申请和释放；完成错误检测和定时器中断功能。

​    最上面的是Test Shell端指令输入模块，这个模块有以下功能：从终端或者测试文件读取命令；将用户需求转换成调度内核函数（即调度进程和资源管理器）；在终端或输出文件中显示结果：如当前运行的进程、错误信息等。

 

![img](https://picgo-w.oss-cn-chengdu.aliyuncs.com/img/clip_image002.png)

图 1 系统总体框架图

 

共设计4大模块

#### \1.    PCB进程控制块

该模块包含进程PCB的数据结构以及操作PCB内部数据变化的接口；

![img](https://picgo-w.oss-cn-chengdu.aliyuncs.com/img/clip_image004.jpg)

图 2 进程控制块结构

#### \2.    RCB资源控制块

该模块包含进程PCB的数据结构以及操作PCB内部数据变化的接口；

![img](https://picgo-w.oss-cn-chengdu.aliyuncs.com/img/clip_image006.jpg)

图 3资源控制块结构

 

#### \3.    进程管理模块

该模块负责管理整个程序的进程池和资源池，对进程进行创建、撤销以及调度等管理任务，对资源进行分配和释放，维护着程序的就绪等待队列、阻塞等待队列，提供了对程序操作的命令接口，基于时间片与高优先抢占调度算法；

优先级：三个优先级就绪队列System-2 User-1 Init-0

#### \4.    Test Shell模块

该模块负责从用户终端Cmd中读取用户命令，完成对用户命令的解释，将用户命令转化为对进程与资源控制的具体操作，调用操作接口API并将执行结果输出到终端中。

这里设计了如下命令：

代码块 1 终端指令集



| 指令 | 解释                                                  |
| ---------------------------------------------------------- | ---------------------------------------------------------- |
| cr   |Create Process eg:cr  <name> <priority>(=1 or 2)      |
| de | Delete Process eg:de  <pName>                          |
| req  | Request Resource eg:req  <resource name> <# of units> |
| rel  |Release Resource  eg:rel <resource name> <# of units> |
| rq   |Show Ready Queue                                      |
| rt   |Show Resource Table                                   |
| pt   |Show Process Table                                    |
| to   |Time Out                                              |
| q   |Quit                                                   |

![img](https://picgo-w.oss-cn-chengdu.aliyuncs.com/img/clip_image008.jpg)

图 4 TestShell模块流程图

### **类设计图**

![img](https://picgo-w.oss-cn-chengdu.aliyuncs.com/img/clip_image010.jpg)

### Test Shell 设计

​    Test Shell 的功能为驱动该管理器工作，即将命令语言（即用户要求）转换成对与内核函数（如create, request等）的调用。

由*代码块**1*可以知道这里设计了创建进程、销毁进程、请求资源、释放资源、查看进程表、查看进程就绪表、查看资源表、时间片切换、帮助、退出等指令。并且不区分大小写。

下面从代码的角度来谈谈入和实现的以上功能：

```java
public static void main(String[] args)
    {
    	initCmd();//初始化一个进程
    	processManagerRun.createResources();
    	//ShowCmdHelp();
        while(quit_flag!=false){    //循环接收用户指令
            System.out.print(">> ");
            command=null;
            command=scanner.nextLine();
            command=command.trim();
            command=command.toLowerCase();
            exeCmd(command);//解析执行指令
            System.out.flush();
	}
}
```

这里使用了flag来判断用户是否想要结束该程序。

```java
// 创建初始进程函数
private static void initCmd()
    {
    	int illegalShow=0;
    	illegalShow=processManagerRun.createProcess("init", 0);//创建初始进程
    	if(illegalShow==1)
    	{
    		System.out.println("init process Created Successed!");
    	}
    	else 
    	{
    		System.out.println("init process Created Default!");
    	}
    }
```

命令行的执行主要是使用switch()语句来实现的，这里主要分析一下req 请求资源指令的解释执行，其他指令也类同，详细的请见源码。

```java
// 命令解释执行函数
private static void exeCmd(String command)
    {
    	String cmds[]=command.split(" ");
    	int key;
		int requestNum = 0;
		switch(cmds[0])
		{
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
……
```

传入命令行后也就是一串字符串，然后按空格将其分成字符数组，当指令为req时就进入改代码块，并进行进一步的分析，如果长度为2就默认`number`为1，并调用`requestResources`请求资源函数，并返回值，这里按返回值做异常处理。`1 - 合法、 2 - 请求资源不存在、 3 - 请求超过此资源总量`。

`private static void ShowCmdHelp()`函数就是一个简单的打印输出了，这里不细说。

 

###  **进程管理器设计**

8.4.1进程状态与操作

进程状态： ready/running/blocked

进程操作： 

  创建(create)： (none) -> ready

  撤销(destroy)： running/ready/blocked -> (none)

  请求资源(Request): running -> blocked (当资源没有时，进程阻塞)

  释放资源(Release): blocked -> ready (因申请资源而阻塞的进程被唤醒)

  时钟中断(Time_out): running -> ready

  调度：ready -> running / running ->ready

####  进程控制块结构（PCB）

•   PID（name）

•   CPU state — not used

•   Memory — not used

•   Open_Files — not used

•   Other_resources //: resource which is occupied

•   Status: Type & List// type: ready, block, running…., //List: RL(Ready list) or BL(block list)

•   Creation_tree: Parent/Children

•   Priority: 0, 1, 2 (Init, User, System)

![img](https://picgo-w.oss-cn-chengdu.aliyuncs.com/img/clip_image012.png)

图 5 PCB结构示意图

 

就绪进程队列：Ready list (RL)

![img](https://picgo-w.oss-cn-chengdu.aliyuncs.com/img/clip_image014.png)

图 6 就绪进程队列示意图

3个级别的优先级，且优先级固定无变化

2 =“system”

1 = “user”

0 = “init”

每个PCB要么在RL中，要么在block list中 。当前正在运行的进程，根据优先级，可以将其放在RL中相应优先级队列的首部。

 

#### 主要函数：

创建进程：
 需要实例化一个PCB类然后初始化各个参数，连接父亲节点和兄弟节点，当前进程为 父亲节点，父亲节点中的子节点为兄弟节点，插入就绪相应优先级队列的尾部。

```java
//创建进程代码实现
	public int createProcess(String pName, int priority)
	{
		int pid =0;
		if(this.checkpName(pName))//是否重名
		{
			return 2;
		}
		pid = this.allocation_pid++;
		PCB pcb = new PCB(pid, pName, processPriorities.values()[priority],runningProcess );
		// 放入进程表
		this.processTable.add(pcb);
		// init进程处理，直接抢占
		if (priority == 0)
		{
			this.runningProcess = pcb;
			runningProcess.changeRunning();
		}
		//放入就绪队列
		switch (priority)
		{
		case 0:
			initReadyList.add(pcb);
			break;
		case 1:
			userReadyList.add(pcb);
			break;
		case 2:
			systemReadyList.add(pcb);
			break;
		default:
			System.out.print("error");
			break;
		}
		//检查优先级是否正确
		if (runningProcess.getPriority() < pcb.getPriority())
		{
			runningProcess.changeReady();
			this.runningProcess = pcb;
			runningProcess.changeRunning();
			System.out.println("[warnning]高优先级抢占,切换进程 " + runningProcess.getPname() + " 运行");
		}
		return 1;
	}

```





![2](https://picgo-w.oss-cn-chengdu.aliyuncs.com/img/clip_image016.png)

图 7 进程间数据结构的关系图

撤销进程：

这里撤销进程会将子进程全部撤销，使用递归的方法来实现这个功能的。并且释放该进程所占的资源。

```java
/**
	 *  撤销进程
	 * @param pName
	 * @return
	 */
	public int destoryProcess(String pName)
	{
		//查看删除进程是否在进程表里
		if(this.checkpName(pName)==false)
			return 2;
		PCB deldata=this.findProcessbyName(pName);
		delChildProcess(deldata);
		return 1;
	}

```



以下是撤销子进程的代码

```java
public int delChildProcess(PCB pcb)
	{
		int temp=-1;
		int tempID=0;
		PCB deldata;
		while(pcb.getpTreeEmpty()==true)
		{
			tempID=pcb.getpTreeFirstChild();
			deldata=this.findProcessbyID(tempID);
			PCB tempPCB=deldata;
			pcb.delChild();
			this.delChildProcess(tempPCB);
		}
		RCB rcb;
		temp=this.freeResource(pcb);
		if(temp!=1)
		{
			System.out.println("error");
		}
		//调度
		if(pcb==runningProcess)// 如是执行态进程 第一个就绪态
		{
			if(systemReadyList.size()==1)// 如果高级system就绪进程队列就剩下执行态进程自己一个 降级
			{
				runningProcess=userReadyList.get(userReadyList.size()-1);
				systemReadyList.remove(0);
			}
			else if(userReadyList.size()==1)
			{
				runningProcess=initReadyList.get(initReadyList.size()-1);
				userReadyList.remove(0);
			}
			else
			{
				this.Schedule();
				if (pcb.getPriority() == 1)
				{
					this.delUserReadyList(pcb);
				}
				else if (pcb.getPriority() == 2)
				{
					this.delSystemReadyList(pcb);
				}
				else
				{

				}
			}
				
		}
		else if (pcb.getType() == "READY")  // 就绪
		{
			if (pcb.getPriority() == 1)
			{
				this.delUserReadyList(pcb);
			}
			else if (pcb.getPriority() == 2)
			{
				this.delSystemReadyList(pcb);
			}
			else   
			{
			}
		}  // 前面已经处理完 阻塞态
		System.out.println( "进程 " + pcb.getPname() + "已撤销!" );
		// 释放 PCB 空间
		this.delProcesstable(pcb);
		pcb.deleteFather();
		return 0;
	}

```



### 资源管理设计

####  主要数据结构

资源的表示：设置固定的资源数量，4类资源，R1，R2，R3，R4，每类资源Ri有i个

资源控制块Resource control block (RCB) 如图8所示

   RID: 资源的ID

 name: 资源的名称

   rStatus: 空闲单元的数量

   Waiting_List: list of blocked process

![img](https://picgo-w.oss-cn-chengdu.aliyuncs.com/img/clip_image018.png)

图 8 资源管理块的RCB

 

#### 请求资源

所有的资源申请请求按照FIFO的顺序进行

 ```java
/**
	 * 请求资源
	 * @param rName	
	 * @param number
	 * @return
	 */
	public int requestResources(String rName,int number)
	{
		//检查是否有此资源,没有就退出
		if(this.checkResourcesName(rName)==false)
		{
			return 2;
		}
		//是否超过资源总量
		if(this.checkResourcesInitnum(rName,number))
		{
			return 3;
		}
		// 根据 rName 找到相应 RCB块
		RCB rcb = this.findResourcesByName(rName);
		
		if (rcb.getNum()>=number)//剩余资源足够
		{
			rcb.requestR(number);//剩余资源减一
			runningProcess.addResourse(number, rcb);//把rcb插入到pcb占有资源列表中
			return 1;
		}
		else//资源不够，阻塞
		{
			runningProcess.changeBLOCKED();// 进程设置 阻塞状态
			runningProcess.changeBLOCKLIST();// 进程加入 阻塞列表
			rcb.addWaitingList(number, runningProcess);// 插入 RCB 资源阻塞等待队列
			
			switch(runningProcess.getPriority())
			{
			case 0:
				initReadyList.remove(0);//删除就绪队列的第一个元素
				blockList.add(runningProcess);
				System.out.println("BUG:init进程阻塞,程序崩溃!" );
				System.exit(0);
			case 1:
				userReadyList.remove(0);
				blockList.add(runningProcess);
				System.out.println( "[warnning]进程 " + runningProcess.getPname() + " 阻塞!" );
				// 降级判断
				if (userReadyList.size() != 0)
				{
					runningProcess = userReadyList.get(0);   // 正在执行进程指针 指向就绪队列第一个
					runningProcess.changeRunning();
				}
				else
				{
					runningProcess = initReadyList.get(0);   // 正在执行进程指针 指向就绪队列第一个
					runningProcess.changeRunning();
				}
				break;
			case 2:
				systemReadyList.remove(0);
				blockList.add(runningProcess);
				System.out.println( "[warnning]进程 " + runningProcess.getPname() + " 阻塞!" );
				// 降级判断
				if (systemReadyList.size() != 0)
				{
					runningProcess = systemReadyList.get(0);   // 正在执行进程指针 指向就绪队列第一个
					runningProcess.changeRunning();
				}
				else
				{
					runningProcess = userReadyList.get(0);   // 正在执行进程指针 指向就绪队列第一个
					runningProcess.changeRunning();
				}
				break;

			default:
				break;
			}
			// 输出进程调度
			System.out.println( "[warnning]切换进程 " + runningProcess.getPname() + " 执行!" );
		}
		return 4;
		
	}

 ```



 

#### 释放资源

如果等待队列不为空就把资源给等待队列的第一个，如果为空自己释放即可。

```java
public int releaseResources(String rName,int number)
	{
		int operand = 0;  // 操作数
		int tempPID = -1;
		int tempNUM = 0;
		
		PCB tempPCB;
		//检查是否有此资源,没有就退出
		if(this.checkResourcesName(rName)==false)
		{
			return 2;
		}
		//是否超过资源总量
		if(this.checkResourcesInitnum(rName,number))
		{
			return 3;
		}
		// 根据 rName 找到相应 RCB块
		RCB rcb = this.findResourcesByName(rName);		
		// release
		
		// 将资源 rcb 从从当前进程 Resources占有资源列表中移除
		// 并资源状态 数量rStatus + number
		operand = runningProcess.delResource(number, rcb);

		if (operand == 0)  // 释放资源数量无效
		{
			return 4;
		} 
		else if (operand == -1) // 该进程无该资源
		{
			return 5;
		}
		else
		{
			rcb.releaseR(operand);  // rStatus + number
		}
		// 跟正在执行进程无关
		// 如果阻塞队列不为空, 且阻塞队列首部进程需求的资源数 req 小于等于可用资源数量 u，则唤醒这个阻塞进程，放入就绪队列
		while((rcb.waitingListEmpty()==false)&&(rcb.isWaitingList()==true))
		{
			rcb.requestR(rcb.getWaitingListFirstNum());//减少请求资源数量
			tempPID=rcb.getWaitingListFirstPID();
			tempNUM=rcb.getWaitingListFirstNum();
			tempPCB=this.findProcessbyID(tempPID);
			PCB changePCB=tempPCB;
			rcb.delWaitingList(); // 从资源的阻塞队列中移除 第一个进程
			changePCB.changeReady();
			changePCB.changeREADYLIST();
			changePCB.addResourse(tempNUM, rcb);
			// 插入 changePCB 到就绪队列 
			// 基于优先级的抢占式调度策略，因此当有进程获得资源时，需要查看当前的优先级情况并进行调度
			switch(changePCB.getPriority())
			{
			case 0:
				this.delBlockList(changePCB);
				initReadyList.add(changePCB);
				System.out.println("进程 " + changePCB.getPname() + " 就绪!" );
				break;
			case 1:
				if(userReadyList.size()==0)
				{
					this.delBlockList(changePCB);
					userReadyList.add(changePCB);
					System.out.println("[warnning]进程 " + changePCB.getPname() + " 就绪!");
					runningProcess = changePCB;  // 高优先级抢占运行
					changePCB.changeRunning();
					System.out.println("[warnning]高优先级进程 " + runningProcess.getPname() + " 抢占" );
				}
				else
				{
					this.delBlockList(changePCB);
					userReadyList.add(changePCB);
					System.out.println("[warnning]进程 " + changePCB.getPname() + " 就绪!");
				}
			case 2:
				if(systemReadyList.size()==0)
				{
					this.delBlockList(changePCB);
					systemReadyList.add(changePCB);
					System.out.println("[warnning]进程 " + changePCB.getPname() + " 就绪!");
					runningProcess = changePCB;  // 高优先级抢占运行
					changePCB.changeRunning();
					System.out.println("[warnning]高优先级进程 " + runningProcess.getPname() + " 抢占" );
				}
				else
				{
					this.delBlockList(changePCB);
					systemReadyList.add(changePCB);
					System.out.println("[warnning]进程 " + changePCB.getPname() + " 就绪!");
				}
			default:
				break;	
			}
		}
		return 1;
	}

```



### **进程调度与时钟中断设计**

调度策略

  基于3个优先级别的调度：2，1，0

  使用基于优先级的抢占式调度策略，在同一优先级内使用时间片轮转（RR）

  基于函数调用来模拟时间共享

  初始进程(Init process)具有双重作用：

1.虚设的进程：具有最低的优先级，永远不会被阻塞  

2.进程树的根

调度代码如下



#### **系统初始化设计**

启动时初始化管理器：

具有3个优先级的就绪队列RL初始化；

Init进程；

4类资源，R1，R2，R3，R4，每类资源Ri有i个

 ```java
	public void Schedule()
	{
		// 当有2级进程
		if (systemReadyList.size() != 0)
		{
			systemReadyList.remove(0);                // 移除system 就绪列表第一个
			systemReadyList.add(runningProcess);  // 把它放入就绪表队列末尾
			runningProcess.changeReady();
			runningProcess.changeREADYLIST();
			runningProcess = systemReadyList.get(0);   // 正在执行进程指针 指向就绪队列第一个
			runningProcess.changeRunning();
		}
		else  // 当没有2级进程的, 只有1级就绪队列
		{
			userReadyList.remove(0);                  // 移除user 就绪列表第一个
			userReadyList.add(runningProcess);    // 把它放入就绪表队列末尾
			runningProcess.changeReady();
			runningProcess.changeREADYLIST();
			runningProcess = userReadyList.get(0);     // 正在执行进程指针 指向就绪队列第一个
			runningProcess.changeRunning();
		}
	}

 ```



## **测试及结果输出展示**

![img](https://picgo-w.oss-cn-chengdu.aliyuncs.com/img/clip_image020.jpg)

图 9 启动时界面

![img](https://picgo-w.oss-cn-chengdu.aliyuncs.com/img/clip_image022.jpg)

图 10 help指令

![img](https://picgo-w.oss-cn-chengdu.aliyuncs.com/img/clip_image024.jpg)

图 11 初始化后的进程表

![img](https://picgo-w.oss-cn-chengdu.aliyuncs.com/img/clip_image026.jpg)

图 12 初始化后的资源表

![img](https://picgo-w.oss-cn-chengdu.aliyuncs.com/img/clip_image028.jpg)

图 13 初始化后的就绪队列

现在执行以下指令：

```
cr x 1   
cr p 1
cr q 1
cr r 1
to  req R2 1
to  req R3 3
to  req R4 3
to  to
req R3 1
req R4 2
req R2 2
to
de q
to
to
```



运行结果如下：

![img](https://picgo-w.oss-cn-chengdu.aliyuncs.com/img/clip_image030.jpg)

图 14 运行结果图

![img](https://picgo-w.oss-cn-chengdu.aliyuncs.com/img/clip_image032.jpg)

图 15 运行结果图

可见这里的运行结果与理论上的完全一致。

 

 
