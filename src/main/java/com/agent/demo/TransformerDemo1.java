package com.agent.demo;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

/**
 * @author ：周超
 * @date ：2022/9/5  15:16
 */
public class TransformerDemo1 implements ClassFileTransformer {

	public static final String editClassName = "com.example.javaagenttest.Hello";
	public static final String editMethod = "test";


	private Instrumentation inst;

	public TransformerDemo1(Instrumentation inst) {
		this.inst = inst;
	}

	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		//使用 JVM 的类搜索路径
		ClassPool cp = ClassPool.getDefault();
		if (classBeingRedefined != null) {
			//如果存在重定义的类，添加额外的类搜索路径
			ClassClassPath ccp = new ClassClassPath(classBeingRedefined);
			cp.insertClassPath(ccp);
		}
		//获取我们需要的Class对象
		try {

			if (className == null) {
				return classfileBuffer;
			}
			final String replace = className.replace("/", ".");
			if (editClassName.equals(replace)) {
				System.out.println("className==" + replace);
				System.out.println("11111111111111111111");
				CtClass ctc = cp.get(editClassName);
				//获取我们需要的Method对象
				CtMethod method = ctc.getDeclaredMethod(editMethod);
				String source = "{System.out.println(\"hello  lyy9,this is test!\");}";
				//setBody：设置方法体
				//insertBefore：插入在方法体最前面
				//insertAfter：插入在方法体最后面
				//insertAt：在方法体的某一行插入内容
				method.setBody(source);
				byte[] bytes = ctc.toBytecode();
				//从 ClassPool 中删除 CtClass 对象
				ctc.detach();
				return bytes;
			} else {
				return classfileBuffer;
			}


		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
}
