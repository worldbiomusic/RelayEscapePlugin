//package com.wbm.plugin.listener;
//
//public class Tttttttttt
//{
//	void copy(Node T) {
//		Node middle = null;
//		if(T != null) {
//			Node left = copy(T.left);
//			Node right = copy(T.right);
//			middle = new Node(T.data, left, right);
//		}
//		
//		return middle;
//	}
//	
//	boolean equals(Node T1, Node T2) {
//		return (T1 == null && T2 == null) 
//				|| ((T1 != null && T2 != null && T1.data == T2.data)
//					&& (equals(T1.left, T2.left) && equals(T1.right, T2.right)));
//	}
//}
