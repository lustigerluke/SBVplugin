
clc
clear all
close all

#variables
languages = {"de","en","fr","es","po","un","bo","ne"};

# constants
# please note if copy-pasting windows paths: there are no backslashes int he path 
folderPath = 'testData/';

searchPathDict = [folderPath , '*_top*'];
searchPathJoke = [folderPath , '*_Witz*'];
dictFiles = glob(searchPathDict)
jokeFiles = glob(searchPathJoke)
for i=1:numel(dictFiles)
  [~, nameI] = fileparts (dictFiles{i});
  
  for j=1:numel(jokeFiles)    
    [~, nameJ] = fileparts (jokeFiles{j});
    zipName = [nameI,nameJ,'.zip'];
    #create tmp folder in testData folder
    tmpFolderPath = [nameI,nameJ]
    mkdir(tmpFolderPath)
    
    #copy dictFiles to folder
    ['copy dictFiles ', nameI, ' , ' nameJ , ' to ' , tmpFolderPath]

    copyfile(dictFiles{i},tmpFolderPath);
    copyfile(jokeFiles{j},tmpFolderPath);
  
    #zip it
    zip(zipName,[tmpFolderPath,'/*']);
    [info, err, msg] = stat (zipName);
    Matrix(i,j) = info.size;
    
    #cleanup - remove tmp folder
    ['remove ' tmpFolderPath]
    delete([tmpFolderPath,'/',nameI,'.txt']);
    delete([tmpFolderPath,'/',nameJ,'.txt']);
    rmdir(tmpFolderPath)
    delete(zipName)
  endfor
endfor


surf(Matrix)
'SUCCESS'