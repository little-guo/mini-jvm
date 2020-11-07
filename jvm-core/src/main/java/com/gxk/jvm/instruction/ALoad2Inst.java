package com.gxk.jvm.instruction;

import com.gxk.jvm.rtda.Frame;
import com.gxk.jvm.rtda.heap.KObject;

public class ALoad2Inst implements Instruction {

  @Override
  public void execute(Frame frame) {
    Object tmp = frame.getRef(2);
    frame.pushRef((KObject) tmp);
  }

  @Override
  public String format() {
    return "aload_2";
  }
}
