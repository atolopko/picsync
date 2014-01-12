package reusablecoder.util.io

import java.io.File;
import scala.collection.mutable;

object DirTree {
  def list(d: File, p: (File) => Boolean): List[File] = {
    if (!d.isDirectory) { return Nil }
    val listing = d.listFiles.toList
    val (dirs, files) = listing.partition(_.isDirectory)
    val rfiles = dirs.flatMap((d: File) => list(d, p))
    files.filter(p) ++ rfiles
  }
  
  def list(d: File): List[File] = {
    list(d, _ => true)
  }

  def createPath(f: File) {
    if (!f.exists) { 
      println("creating directory path " + f)
      f.mkdirs()
    }
    else {
      if (!f.isDirectory) {
        error("cannot create directory " + f + " due to extant file of same name");
      } 
    }
  }  

  def collate(srcDir: File,
              srcFileFilter: (File) => Boolean,
              destDir: File,
              collateFunc: (File) => File) : (collection.Map[File,File], collection.Map[File,Exception]) = {
    var copied = mutable.Map[File,File]()
    var errors = mutable.Map[File,Exception]()
    list(srcDir, srcFileFilter).foreach((f: File) => { 
      val destFile = new File(new File(destDir, collateFunc(f).toString), f.getName)
      createPath(destFile.getParentFile)
      try {
        org.apache.commons.io.FileUtils.copyFile(f, destFile)
        println("copied " + f + " to " + destDir)
        copied(f) = destFile
      }
      catch {
        case e: Exception => errors(f) = e
      }
    })
    return (copied, errors)
  }
}
