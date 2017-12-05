clear all
load('imgsnlabels.mat','imgs','labels');
k=10;
[idx C] = kmeans(imgs,k,'MaxIter',500);
for i=1:5000
    for j = 1:10
        if(labels(i,j) == 1)
            digit(i) = j - 1;
        end
    end
end
digit=transpose(digit);
correctclass=0;
in = 0;
for j=1:k
    cluster = [];
    digit1 = [];
    in = 1;
    for i = 1:size(idx,1)
        if(idx(i) == j);
            cluster(in)=i;
            digit1(in)=digit(cluster(in));
            in = in + 1;
            
        end
    end
    m(j)=mode(transpose(digit1));
    b=m(j);
    c = 0;
    for i = 1:size(digit1,2)
        if(digit1(i) == b)
            c = c + 1;
        end
        for u = 0:9
            z=sum(digit1==u);
            clusterpv(j,u+1)=z;
        end
    end
    truepv(j)=c;
    correctclass=correctclass+c; 
end
accuracy=(correctclass/5000)*100
size(digit1);
clusterpv
save('centroid5.mat','idx','C');
save('clusterpv.mat','clusterpv');