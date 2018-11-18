
# clear console, clear variables, close all open figures
clc
clear all
close all

# in this example we will use following languages
# languages = {"de","en","fr","es","po","un","bo","ne"};

# constants
# please note:copy-pasting windows paths: there are no backslashes in the path 
folderPath = 'cutTestData/*';

# initialize result matrix
resultMatrix =zeros(8);


# loop through data folders
folders = glob(folderPath)
for x=1:numel(folders)
  [~, nameI] = fileparts (folders{x})
  for y=1:numel(folders)
    [~, nameI] = fileparts (folders{y})
    
    # just calculate matrix for different data sets
    if (!strcmp(folders{x},folders{y}))
      
      folderPath1 = [folders{x} , '/*'];
      folderPath2 = [folders{y} , '/*'];
      filesOfFolder1 = glob(folderPath1);
      filesOfFolder2 = glob(folderPath2);
      
      # for every element in data folder
      for i=1:numel(filesOfFolder1)
        [~, nameI] = fileparts (filesOfFolder1{i});
        #get file size
        [info, err, msg] = stat (filesOfFolder1{i});
        file1Size = info.size;
        
        #CREATE zip of file
        tmpFolderPath = nameI;
        mkdir(tmpFolderPath);
        copyfile(filesOfFolder1{i},tmpFolderPath);
        firstZipName = [nameI, '.zip']
        zip(firstZipName,[tmpFolderPath,'/*']);
        #get zip size 
        [info, err, msg] = stat (firstZipName);
        file1file2ZipSize = info.size;
        
        # calculate compression rate
        file1CompressionRate = file1file2ZipSize / file1Size;
        
        #delete zip and folder as we just need the size for calculation
        delete([tmpFolderPath,'/',nameI,'.txt']);
        rmdir(tmpFolderPath);
        delete(firstZipName);
          
        for j=1:numel(filesOfFolder2)    
          [~, nameJ] = fileparts (filesOfFolder2{j});
          zipName = [nameI,nameJ,'.zip'];
          #create tmp folder in testData folder
          tmpFolderPath = [nameI,nameJ];
          mkdir(tmpFolderPath);
          
          #copy filesOfFolder1 to folder
          ['copy filesOfFolder1 ', nameI, ' , ' nameJ , ' to ' , tmpFolderPath];
          copyfile(filesOfFolder1{i},tmpFolderPath);
          copyfile(filesOfFolder2{j},tmpFolderPath);
          
          # get folder size (add jokeFile size to filesOfFolder1ize)
          [info, err, msg] = stat (filesOfFolder2{i});
          file2Size = file1Size + info.size;
          
          #zip it and get size of zip
          zip(zipName,[tmpFolderPath,'/*']);
          [info, err, msg] = stat (zipName);
          file2ZipSize = info.size;

          #calculate compression rate
          compressionRate = file2ZipSize / file2Size ;
          
          #calculate exprected rate
          expectedfile2ZipSize = file2Size * file1CompressionRate;
          
          %Matrix(i,j) = (expectedfile2ZipSize - file2ZipSize) /  file2ZipSize;
          kompressionDict = (file1Size -file1file2ZipSize) / file1Size;
          kompressionBoth = (file2Size - file2ZipSize) / file2Size;
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

      resultMatrix = resultMatrix .+ Matrix;
      
    else # dont calculate anything if the folders are the same
      ["skip " folders{x}]
    endif
    
    
  endfor
endfor

    figure()
    surf(resultMatrix)
    view(2)
    xlabel("name of dict language");
    ylabel("name of text language");
    zlabel(" diff of compression rates");
    'SUCCESS'

