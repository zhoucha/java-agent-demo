package com.agent.demo;

import com.sun.tools.attach.VirtualMachine;

import java.lang.instrument.Instrumentation;

/**
 * @author ：周超
 * @date ：2022/9/5  14:25
 */
public class Test {
	/**
	 * jvm 参数形式启动，运行此方法
	 *
	 * @param agentArgs
	 * @param inst
	 */
	public static void premain(String agentArgs, Instrumentation inst) {
		System.out.println("premain");
		try {
			//inst.appendToBootstrapClassLoaderSearch(new JarFile(new File("C:\\idea-work\\java-agent-demo\\target\\java-agent-demo-1.0-SNAPSHOT-jar-with-dependencies.jar")));
			inst.addTransformer(new TransformerDemo1(inst), true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (Class aClass : inst.getAllLoadedClasses()) {
			System.out.println(aClass.getName().replace("/", "."));
			if (aClass.getName().equals(TransformerDemo1.editClassName)) {
				try {
					inst.retransformClasses(new Class[]{aClass});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * 动态 AttachDemo 方式启动，运行此方法
	 *
	 * @param agentArgs
	 * @param inst
	 */
	public static void agentmain(String agentArgs, Instrumentation inst) {
		System.out.println("agentmain");
		Class[] classes = inst.getAllLoadedClasses();
		// 判断类是否已经加载
		try {

			for (Class aClass : classes) {
				System.out.println(aClass.getName());
				if (aClass.getName().equals(TransformerDemo1.editClassName)) {
					// 添加 Transformer
					inst.addTransformer(new TransformerDemo1(inst), true);
					// 触发 Transformer
					inst.retransformClasses(aClass);
					System.out.println("触发 Transformer");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {

		VirtualMachine vm = null;
		try {
			vm = VirtualMachine.attach("8964");
			vm.loadAgent("C:\\idea-work\\java-agent-demo\\target\\java-agent-demo-1.0-SNAPSHOT-jar-with-dependencies.jar");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}


	}

}
