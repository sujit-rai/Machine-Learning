clear all
fid = fopen('data.txt','r');
lid = fopen('label.txt','r');
label = fgetl(lid);
string = fgetl(fid);
i = 1;
imgs = zeros(5000,400);
labels = zeros(5000,10);
while size(string,2)>1
    split = regexp(string,',','split');
    imgs(i,:) = cellfun(@str2double,split);
    split = regexp(label,',','split');
    labels(i,:) = cellfun(@str2num,split);
    i = i + 1
    string = fgetl(fid);
    label = fgetl(lid);
end
k=10;
save('imgsnlabels.mat','imgs','labels');
idx = kmeans(imgs,k);
