package PCB;

import java.util.List;

public class RCB {
	private int rid;                      // 资源ID
	private String rName;                 // 资源名
	private int rStatus;                  // 资源状态 数量
	private int initNum;                  // 初始化数量
	private List<waiting> waitingList;    // 资源阻塞等待队列
}
