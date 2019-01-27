// ASM: a very small and fast Java bytecode manipulation framework
// Copyright (c) 2000-2011 INRIA, France Telecom
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions
// are met:
// 1. Redistributions of source code must retain the above copyright
//    notice, this list of conditions and the following disclaimer.
// 2. Redistributions in binary form must reproduce the above copyright
//    notice, this list of conditions and the following disclaimer in the
//    documentation and/or other materials provided with the distribution.
// 3. Neither the name of the copyright holders nor the names of its
//    contributors may be used to endorse or promote products derived from
//    this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
// THE POSSIBILITY OF SUCH DAMAGE.
package org.objectweb.asm.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.zip.GZIPInputStream;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * A tool to transform classes in order to make them compatible with Java 1.5. The original classes
 * can either be transformed "in place", or be copied first to destination directory and transformed
 * here (leaving the original classes unchanged).
 *
 * @author Eric Bruneton
 * @author Eugene Kuleshov
 */
public class Retrofitter {

  /**
   * Transforms the source class file, or if it is a directory, its files (recursively), in place,
   * in order to make them compatible with the JDK 1.5.
   *
   * @param src source file or directory.
   * @throws IOException if the source files can't be read or written.
   */
  public void retrofit(final File src) throws IOException {
    retrofit(src, null);
  }

  /**
   * Transforms the source class file, or if it is a directory, its files (recursively), either in
   * place or into the destination file or directory, in order to make them compatible with the JDK
   * 1.5.
   *
   * @param src source file or directory.
   * @param dst optional destination file or directory.
   * @throws IOException if the source or destination file can't be read or written.
   */
  public void retrofit(final File src, final File dst) throws IOException {
    if (src.isDirectory()) {
      File[] files = src.listFiles();
      if (files == null) {
        throw new IOException("Unable to read files of " + src);
      }
      for (File file : files) {
        retrofit(file, dst == null ? null : new File(dst, file.getName()));
      }
    } else if (src.getName().endsWith(".class")) {
      if (dst == null || !dst.exists() || dst.lastModified() < src.lastModified()) {
        ClassReader classReader = new ClassReader(Files.newInputStream(src.toPath()));
        ClassWriter classWriter = new ClassWriter(0);
        ClassRetrofitter classRetrofitter = new ClassRetrofitter(classWriter);
        classReader.accept(classRetrofitter, ClassReader.SKIP_FRAMES);

        if (dst != null && !dst.getParentFile().exists() && !dst.getParentFile().mkdirs()) {
          throw new IOException("Cannot create directory " + dst.getParentFile());
        }
        OutputStream outputStream = Files.newOutputStream((dst == null ? src : dst).toPath());
        try {
          outputStream.write(classWriter.toByteArray());
        } finally {
          outputStream.close();
        }
      }
    }
  }

  /** A ClassVisitor that retrofits classes from 1.6 to 1.5 version. */
  static class ClassRetrofitter extends ClassVisitor {

    public ClassRetrofitter(final ClassVisitor classVisitor) {
      super(Opcodes.ASM4, classVisitor);
    }

    @Override
    public void visit(
        final int version,
        final int access,
        final String name,
        final String signature,
        final String superName,
        final String[] interfaces) {
      super.visit(Opcodes.V1_5, access, name, signature, superName, interfaces);
    }
  }
}
