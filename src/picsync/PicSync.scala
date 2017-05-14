package picsync

import java.io.File;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import scala.collection.immutable.Set;
import reusablecoder.util.io.DirTree;


object PicSync {
  val LastCopiedTimeStampFileName = ".picsync"
  
  def main(args : Array[String]) : Unit = {
    val srcDir = new File(args(0))
    val destDir = new File(args(1))
    val lastSyncTimeStamp = 
      if (args.size > 2) ISODateTimeFormat.dateParser.parseMillis(args(2)) 
      else readTimeStamp(destDir)
    println("synchronizing files after " + new DateTime(lastSyncTimeStamp))
    var newSyncTimeStamp = 0L;

    val (files, errors) =
      DirTree.collate(srcDir, 
                      _.lastModified > lastSyncTimeStamp,
                      destDir, 
                      imageFileParentDirectoryPath)
    files.values.foreach((f: File) => { 
      f.setReadOnly()
      println("set " + f + " to readonly")
      newSyncTimeStamp = f.lastModified max newSyncTimeStamp
    })
      
    if (files.size > 0) {
      recordTimeStamp(destDir, newSyncTimeStamp)
      println("set sync time stamp to " + new DateTime(newSyncTimeStamp))
    }
    println("n=" + files.size)
    println("errors=" + errors.size)
    //if (errors.size > 0) println(errors.)
  }
  
  def imageFileParentDirectoryPath(imageFile: File) : File = {
    new File(new DateTime(imageFile.lastModified).toString("yyyy/MM/dd"))    
  }
  
  def readTimeStamp(dir: File): Long = {
    val timeStampFile = new File(dir, LastCopiedTimeStampFileName)
    try {
      val timeStampStr = org.apache.commons.io.FileUtils.readFileToString(timeStampFile, "UTF8")
      return timeStampStr.toLong
    }
    catch {
      case e: Exception => {
        println("warning: last sync time stamp not found at " + timeStampFile)
        return 0;
      }
    }
  }

  def recordTimeStamp(dir: File, timeStamp: Long) {
    org.apache.commons.io.FileUtils.writeStringToFile(new File(dir, LastCopiedTimeStampFileName), timeStamp.toString, "UTF8")
  }
}

class PicSyncTestSuite extends org.scalatest.FunSuite {
  test("imageFileParentDirectoryPath") {
    val f = File.createTempFile("picsync", ".txt")
    f.setLastModified(new DateTime(2010, 1, 3, 0, 0, 0, 0).getMillis)
    val expected = new File("2010/01/03")
    assert(PicSync.imageFileParentDirectoryPath(f) === expected)
  }
  
  test("read missing timestamp") {
    val timeStampFile = new File("/tmp/", PicSync.LastCopiedTimeStampFileName)
    timeStampFile.delete()
    assert(timeStampFile.exists === false)
    assert(PicSync.readTimeStamp(timeStampFile) === 0)
  }

  test("read/write timestamp") {
    PicSync.recordTimeStamp(new File("/tmp/"), new DateTime(2010, 1, 3, 0, 0, 0, 0).getMillis)
    assert(new File("/tmp/", PicSync.LastCopiedTimeStampFileName).exists)
    assert(new DateTime(PicSync.readTimeStamp(new File("/tmp"))) === new DateTime(2010, 1, 3, 0, 0, 0, 0))
  }
  
}



