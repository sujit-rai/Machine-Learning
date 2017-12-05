clear all
load('sample.mat','img','label');

llabel = zeros(5000,10);
for i = 1:5000
    
    llabel(i,label(i)+1) = 1;
end



a = -0.01;
b = 0.01;
w= randn(1,25)*(sqrt(2/(32*32 + 28*28)));
b1 = randn(1,1);
w2 = randn(64,576)*(sqrt(2/(32*32 + 28*28)));
b2 = randn(64,1);
w3 = randn(10,64)*(sqrt(2/(32*32 + 28*28)));
b3 = randn(10,1);

eta = 0.001;

decay = 0.9;
eps = 10^-9;

delb2 = zeros(1,1);
db22 = zeros(64,1);
db32 = zeros(10,1);
delw2 = zeros(1,25);
dw22 = zeros(64,576);
dw32 = zeros(10,64);

m = 0.9;

for epoch = 1:1000
    epoch
    err = 0;
    verr= 0;
    tic
    for j = 1:50
        for i = 1:100
            I(:,((i-1)*576)+1:i*576) = im2col(img(:,:,((j-1)*100)+i),[5 5],'sliding');
            labels(i,:) = llabel(((j-1)*100)+i,:);
        end
            
        y = w*I;
        
        y = y + repmat(b1,[1,57600]);
        y(find(y<0))= 0;
        
        
        
        in = reshape(y,[576 100]);
        
        
        f3 = arrayfun(@sigmoid,(w2*in)+repmat(b2,1,size(in,2)));
        f4 = (w3*f3)+repmat(b3,1,size(f3,2));
        
        
        
        for k = 1:size(f4,2)
            exps = exp(f4(:,k));
            s(:,k) = exps/sum(exps);
            
        end
        
        
        df4 = s - labels';
        
        
        if j>500
            verr = verr + sum(sum(abs(df4)));
            continue;
        end
        
        %backpropagation
        err = err + sum(sum(abs(df4)));
        
        
        
        dw3 = (df4*f3');
        db3 = sum(df4,2);
        df3 = w3'*df4;
        db2 = (df3.*f3.*(1-f3));
        dw2 = (db2*in');
        din = w2'*db2;
        
        dely = reshape(din,[1 57600]);
        dely(find(y<0))= 0;
        delb = sum(dely);
        
        
        
        delw = dely*I';
        
        db22 = (decay*(db22)) + ((1-decay)*(sum(db2,2).^2));
        delb2 = (decay*delb2) + ((1-decay)*(delb.^2));
        db32 = (decay*db32) + ((1-decay)*(db3.^2));
        delw2 = (decay*delw2) + ((1-decay)*(delw.^2));
        dw22 = (decay*dw22) + ((1-decay)*(dw2.^2));
        dw32 = (decay*dw32) + ((1-decay)*(dw3.^2));
        

        w3 = w3 - (eta*(((dw32 + eps).^-0.5).*dw3));
        b3 = b3 - (eta*(((db32 + eps).^-0.5).*db3));
        w2 = w2 - (eta*(((dw22 + eps).^-0.5).*dw2));
        b2 = b2 - (eta*(((db22 + eps).^-0.5).*sum(db2,2)));
        w = w - (eta*(((delw2 + eps).^-0.5).*delw));
        b1 = b1 - (eta*(((delb2 + eps).^-0.5).*delb));
        
        
    end
    toc
    err = err/5000
    verr = verr/5000
    end
    
    
    
   
