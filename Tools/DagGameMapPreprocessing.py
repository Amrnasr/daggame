import pygame
import os.path
import glob

def GenerateTileMap(graphic,tileWidth,tilesPerRow,tilesPerColumn):
    tileMap = {}
    for j in range (0,int(tilesPerColumn)): #comprobar que se lee y guarda en el sentido que toca
        for i in range (0,int(tilesPerRow)):
            count=0
            for l in range (0,tileWidth):
                for k in range (0,tileWidth):
                    pixel= graphic.get_at((i*tileWidth+k,j*tileWidth+l))
                    if not(pixel.r == 0 and pixel.g == 0 and pixel.b == 0):
                        count+=1
            if count >= (tileWidth*tileWidth*50) / 100 :           
                tileMap[j*tilesPerRow+i]=count
            else:
                tileMap[j*tilesPerRow+i]=0
    return tileMap

def SaveTileMap(tileMap,fileName,tilesPerRow,tilesPerColumn):
    f = open(fileName, "w")

    for j in range (0, int(tilesPerColumn)):
        for i in range (0, int(tilesPerRow)):
            f.write(str(tileMap[j*tilesPerRow + i]))
            if i != int(tilesPerRow)-1:
                f.write(" ")
        if j != int(tilesPerColumn)-1:
            f.write("\n")

    f.close()


    
x='a'
while x != 'i' and x != 'o':
    x = input("File(i) or Folder(o)? (i\o): ")

name = input("Write its name:")

tileWidth = int(input("Write the tile width:"))

if (x=='i'):
    graphic = pygame.image.load(name)

    width, height = graphic.get_size()

    tilesPerColumn = height/tileWidth
    tilesPerRow = width/tileWidth
    
    tileMap = GenerateTileMap(graphic,tileWidth,tilesPerRow,tilesPerColumn)
    splitName = os.path.splitext(name)
    outputName=name.replace(splitName[1], "tilemap.txt")
    SaveTileMap(tileMap,outputName,tilesPerRow,tilesPerColumn)
else: #probar que vaya con carpetas
    f = glob.iglob(os.path.join(name, '*.*'))

    for filePath in glob.glob( os.path.join(name, '*.*') ):

        graphic = pygame.image.load(filePath)

        width, height = graphic.get_size()

        
        tilesPerColumn = height/tileWidth
        tilesPerRow = width/tileWidth
        
        tileMap = GenerateTileMap(graphic,tileWidth,tilesPerRow,tilesPerColumn)
        
        index=filePath.rfind("\\")
        fileName=filePath[index+1:len(filePath)]
        splitName = os.path.splitext(fileName)
        outputName=fileName.replace(splitName[1], "tilemap.txt")

        print(outputName)
        
        SaveTileMap(tileMap,outputName,tilesPerRow,tilesPerColumn)     
    
    
    








        
            
        
