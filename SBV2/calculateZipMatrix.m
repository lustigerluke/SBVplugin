
clc
clear all
close all

#variables
languages = {"de","en","fr","es","po","un","bo","ne"};

# constants
# please note if copy-pasting windows paths: there are no backslashes int he path 
folderPath = 'ntestData/';


searchPathDict = [folderPath , '*_top*'];
searchPathJoke = [folderPath , '*_Witz*'];
dictFiles = glob(searchPathDict)
jokeFiles = glob(searchPathJoke)
for i=1:numel(dictFiles)
  [~, nameI] = fileparts (dictFiles{i});
  #get dict file size
  [info, err, msg] = stat (dictFiles{i});
  dictFileSize = info.size;
  
  #CREATE zip for dictFile
  tmpFolderPath = nameI;
  mkdir(tmpFolderPath);
  copyfile(dictFiles{i},tmpFolderPath);
  firstZipName = [nameI, '.zip']
  zip(firstZipName,[tmpFolderPath,'/*']);
  #get zip size 
  [info, err, msg] = stat (firstZipName);
  dictZipSize = info.size;
  
  # calculate compression rate
  dictFileCompressionRate = dictZipSize / dictFileSize;
  
  #delete zip and folder as we just need the size for calculation
  delete([tmpFolderPath,'/',nameI,'.txt']);
  rmdir(tmpFolderPath);
  delete(firstZipName);
    
  for j=1:numel(jokeFiles)    
    [~, nameJ] = fileparts (jokeFiles{j});
    zipName = [nameI,nameJ,'.zip'];
    #create tmp folder in testData folder
    tmpFolderPath = [nameI,nameJ];
    mkdir(tmpFolderPath);
    
    #copy dictFiles to folder
    ['copy dictFiles ', nameI, ' , ' nameJ , ' to ' , tmpFolderPath]
    copyfile(dictFiles{i},tmpFolderPath);
    copyfile(jokeFiles{j},tmpFolderPath);
    
    # get folder size (add jokeFile size to dictFileSize)
    [info, err, msg] = stat (jokeFiles{i});
    plainSize = dictFileSize + info.size;
    
    #zip it and get size of zip
    zip(zipName,[tmpFolderPath,'/*']);
    [info, err, msg] = stat (zipName);
    zipSize = info.size;

    #calculate compression rate
    compressionRate = zipSize / plainSize ;
    
    #calculate exprected rate
    expectedZipSize = plainSize * dictFileCompressionRate;
    
    %Matrix(i,j) = (expectedZipSize - zipSize) /  zipSize;
    kompressionDict = (dictFileSize -dictZipSize) / dictFileSize;
    kompressionBoth = (plainSize - zipSize) / plainSize;
    kompressionsDelta = abs(kompressionBoth - kompressionDict);
    Matrix(i,j) = kompressionsDelta;
    
    
    #cleanup - remove tmp folder
    ['remove ' tmpFolderPath];
    delete([tmpFolderPath,'/',nameI,'.txt']);
    delete([tmpFolderPath,'/',nameJ,'.txt']);
    rmdir(tmpFolderPath);
    delete(zipName);
  endfor
endfor


surf(Matrix)
view(2)
xlabel("name of dict language");
ylabel("name of text language");
zlabel(" diff of compression rates");
'SUCCESS'


