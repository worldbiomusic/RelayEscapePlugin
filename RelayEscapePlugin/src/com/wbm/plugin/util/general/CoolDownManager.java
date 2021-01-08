package com.wbm.plugin.util.general;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.wbm.plugin.Main;

public class CoolDownManager {
    /*
     * 여러 주제에 대한 쿨 다운을 관리해주는 클래스
     * 
     * -등록해서 사용 가능
     * 
     * -addPlayer()메소드 1개만 가지고 사용이 가능함(반환값을 가지고 판단해서 사용) 
     * 
     * -> 반환값 true일때: 리스트에 플레이어가 없으므로 실행하게 하면 됨, 그리고 리스트에 player가 올라감 
     * 
     * -> 반환값 false일때: 이미 리스트에 플레이어가 있으므로 실행을 막으면 됨
     */

    // 주제의 쿨 다운 타미 세팅 값
    private static Map<String, Integer> subjectTime = new HashMap<>();

    // 주제의 쿨 다운 타임 플레이어 관리 리스트
    private static Map<String, List<Player>> subjects = new HashMap<>();

    public static void registerSubject(String subject, int subjectTime) {
	// 주제 시간 등록
	CoolDownManager.subjectTime.put(subject, subjectTime);

	// 주제 리스트 생성
	CoolDownManager.subjects.put(subject, new ArrayList<>());
    }

    public static boolean addPlayer(String subject, Player p) {
	// 주제가 존재할때
	if (subjectTime.containsKey(subject)) {
	    // list에 player가 있으면 처리 x (등록 실패: false 반환)
	    if ((CoolDownManager.subjects.get(subject).contains(p))) {
		return false;
	    }

	    // list에 삽입
	    CoolDownManager.subjects.get(subject).add(p);

	    // 주제 시간후에 자동으로 리스트에서 삭제
	    Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {

		@Override
		public void run() {
		    CoolDownManager.subjects.get(subject).remove(p);
		}
	    }, 20 * CoolDownManager.subjectTime.get(subject));

	    // 등록 성공: true 반환
	    return true;
	}

	// subject가 없어도 false
	BroadcastTool.reportBug(subject + " is not registered!");
	return false;
    }

    public static boolean hasPlayer(String subject, Player p) {
	if (CoolDownManager.subjectTime.containsKey(subject)) {
	    return CoolDownManager.subjects.get(subject).contains(p);
	}
	return false;
    }
    
//    public static int getRemainingTime(String subject, Player p) {
//	if(CoolDownManager.subjectTime.containsKey(subject)) {
//	    if(CoolDownManager.subject.get(subject).contains(p)) {
//		return CoolDownManager.subject.get(subject).get(p);
//	    }else {
//		return 0;
//	    }
//	}
//	
//	BroadcastTool.reportBug(subject + " is not registered!");
//	return -1;
//    }
}

//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
