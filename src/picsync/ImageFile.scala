package picsync

class Image(fileIn: java.io.File) {
  
  val files = List[ImageFile]()
  val name = fileIn.getName()
  val date = new java.util.Date(fileIn.lastModified());
  
  def getFiles : List[ImageFile] = { return files }
  
  class ImageFile(uri: String) {
    
  }
  
}
