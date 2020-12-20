package PCB;

public class waiting {
	int BlockPID;           // 阻塞的进程ID
	int BlockNum;           // 阻塞的资源数量
	PCB pcb;             // 指向PCB块

	waiting(int BlockPID, int BlockNum, PCB* pcb)
	{
		this.BlockPID = BlockPID;
		this.BlockNum = BlockNum;
		this.pcb = pcb;
	}
}
