package com.gxk.jvm.classfile;

import com.gxk.jvm.classfile.cp.ClassCp;
import com.gxk.jvm.classfile.cp.FieldDef;
import com.gxk.jvm.classfile.cp.IntegerCp;
import com.gxk.jvm.classfile.cp.MethodDef;
import com.gxk.jvm.classfile.cp.NameAndType;
import com.gxk.jvm.classfile.cp.StringCp;
import com.gxk.jvm.instruction.*;
import com.gxk.jvm.util.Utils;

import java.io.DataInputStream;
import java.io.IOException;

public abstract class InstructionReader {

  public static Instruction read(int opCode, DataInputStream stream, ConstantPool constantPool) throws IOException {
    switch (opCode) {
      case 0x0:
        return new NopInst();
      case 0x3:
        return new Iconst0Inst();
      case 0x4:
        return new Iconst1Inst();
      case 0x5:
        return new Iconst2Inst();
      case 0x6:
        return new Iconst3Inst();
      case 0x3b:
        return new Istore0Inst();
      case 0x3c:
        return new Istore1Inst();
      case 0x3d:
        return new Istore2Inst();
      case 0x10:
        return new BiPushInst(stream.readByte());
      case 0x9a:
        return new IfneInst(stream.readShort());
      case 0xa3:
        return new IfIcmpGtInst(stream.readShort());
      case 0x9f:
        return new IfIcmpEqInst(stream.readShort());
      case 0xa0:
        return new IfIcmpNeInst(stream.readShort());
      case 0x1a:
        return new Iload0Inst();
      case 0x1b:
        return new Iload1Inst();
      case 0x1c:
        return new Iload2Inst();
      case 0x60:
        return new IaddInst();
      case 0x64:
        return new ISubInst();
      case 0x84:
        return new IIncInst(stream.readUnsignedByte(), stream.readUnsignedByte());
      case 0xa7:
        return new Goto1Inst(stream.readShort());
      case 0xac:
        return new IreturnInst();
      case 0xb1:
        return new ReturnInst();
      case 0xb2:
        int gsIndex = stream.readUnsignedShort();
        ConstantInfo gsInfo = constantPool.infos[gsIndex - 1];
        FieldDef fieldDef = (FieldDef) gsInfo;
        int gsClassIndex = fieldDef.getClassIndex();
        int gsClassNameIndex = ((ClassCp) constantPool.infos[gsClassIndex - 1]).getNameIndex();
        int gsNTIdx = fieldDef.getNameAndTypeIndex();
        NameAndType gsNT = (NameAndType) constantPool.infos[gsNTIdx - 1];
        return new GetstaticInst(Utils.getString(constantPool, gsClassNameIndex), Utils.getString(constantPool, gsNT.getNameIndex()), Utils.getString(constantPool, gsNT.getDescriptionIndex()));
      case 0xb3:
        int psIndex = stream.readUnsignedShort();
        ConstantInfo psInfo = constantPool.infos[psIndex - 1];
        FieldDef psFieldDef = (FieldDef) psInfo;
        int psClassIndex = psFieldDef.getClassIndex();
        int psClassNameIndex = ((ClassCp) constantPool.infos[psClassIndex - 1]).getNameIndex();
        int psNTIdx = psFieldDef.getNameAndTypeIndex();
        NameAndType psNT = (NameAndType) constantPool.infos[psNTIdx - 1];
        return new PutStaticInst(Utils.getString(constantPool, psClassNameIndex), Utils.getString(constantPool, psNT.getNameIndex()), Utils.getString(constantPool, psNT.getDescriptionIndex()));
      case 0x12:
        int index = stream.readUnsignedByte();
        ConstantInfo info = constantPool.infos[index - 1];
        switch (info.infoEnum) {
          case CONSTANT_String:
            int stringIndex = ((StringCp) info).stringIndex;
            String string = Utils.getString(constantPool, stringIndex);
            return new LdcInst(null, string);
          case CONSTANT_Integer:
            return new LdcInst(((IntegerCp) info).val, null);
        }
        throw new IllegalStateException();
      case 0xb6:
        return new InvokespecialInst(stream.readUnsignedShort());
      case 0xb8:
        ConstantInfo methodinfo = constantPool.infos[stream.readUnsignedShort() - 1];
        MethodDef methodDef = (MethodDef) methodinfo;
        NameAndType nat = (NameAndType) constantPool.infos[methodDef.nameAndTypeIndex - 1];

        String methodName = Utils.getString(constantPool, nat.getNameIndex());
        String descriptor = Utils.getString(constantPool, nat.getDescriptionIndex());
        return new InvokestaticInst(methodName, descriptor);
      default:
        return null;
//        throw new UnsupportedOperationException("unknown op code");
    }

  }
}
