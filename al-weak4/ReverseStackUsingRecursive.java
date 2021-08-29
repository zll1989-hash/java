package com.sankuai.inf.leaf.server.Algo.weak4;

import java.util.Stack;
/**
 * 进行栈的反转
 * @author Lucien
 */
public class ReverseStackUsingRecursive {

	public static void reverse(Stack<Integer> stack) {
		// 判断stack是否为空
		if(stack.isEmpty()){
			return;
		}
		int i = f(stack);
		reverse(stack);
		stack.push(i);
	}

    /**
     *
	 * 栈底元素移除掉
	 * 上面的元素盖下来
	 * 返回移除掉的栈底元素
	 *
	 * @param stack
     * @return
     */
	public static int f(Stack<Integer> stack){

		int result = stack.pop();
		if(stack.isEmpty()){
			return result;
		}else {
			int last = f(stack);
			stack.push(result);
			return last;
		}

	}


	public static void main(String[] args) {
		Stack<Integer> test = new Stack<Integer>();

		test.push(1);
		test.push(2);
		test.push(3);
		test.push(4);
		test.push(5);
		reverse(test);
		while (!test.isEmpty()) {
			System.out.println(test.pop());
		}

	}

}
