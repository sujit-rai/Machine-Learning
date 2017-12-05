clear all
load('sample.mat','img','label');


k1 = double(randn(5,5,6)*(sqrt(2/(32*32 + 28*28))));
b1 = double(randn(1,6)*(sqrt(2/(32*32 + 28*28))));
k2 = double(randn(5,5,6,16)*(sqrt(2/(14*14 + 10*10))));
b2 = double(rand(1,16)*(sqrt(2/(14*14 + 10*10))));
w1 = double(randn(128,4*4*16)*(sqrt(2/(400 + 120))));
bw1 = double(randn(128,1)*(sqrt(2/(400 + 120))));
w2 = double(randn(64,128)*(sqrt(2/(64 + 128))));
bw2 = double(randn(64,1)*(sqrt(2/(64 + 128))));
w3 = double(randn(10,64)*(sqrt(2/(64 + 1))));
bw3 = double(randn(10,1)*(sqrt(2/(64 + 1))));

labels = zeros(5000,10);
for i = 1:5000
    labels(i,label(i)+1) = 1;
end


dk1 = zeros(size(k1));
dk2 = zeros(size(k2));
dw1 = zeros(size(w1));
dw2 = zeros(size(w2));
dw3 = zeros(size(w3));
db1 = zeros(size(b1));
db2 = zeros(size(b2));
dbw1 = zeros(size(bw1));
dbw2 = zeros(size(bw2));
dbw3 = zeros(size(bw3));

decay = 0.1;

eps = 10^-9;

eta = 0.01;
for epoch = 1:1000
    epoch
    err = 0;
    verr = 0;
    for i = 1:size(img,3)
        if(mod(i,1000) == 0)
            fprintf(int2str(1));
        end
        I = img(:,:,i);
        Y = labels(i,:);
        
        C1 = zeros(24,24,6);
        %convolution layer C1
        for j = 1:6
            C1(:,:,j) = convn(I,k1(:,:,j),'valid') + b1(j);
        end
        
        %ReLU
        R1 = C1;
        R1(find(R1<0)) = 0;
        P1 = zeros(12,12,6);
        %max polling P1
        for q = 1:6
            for u = 1:12
               for v = 1:12
                   a = sort(sort(R1(2*u-1:2*u,2*v-1:2*v,q),1,'descend'),2,'descend');
                   P1(u,v,q) = a(1,1);
               end
            end
        end
        
        
        
        %convolution layer C2
        C2 = zeros(8,8,16);
        for j = 1:16
            C2(:,:,j) = convn(P1,k2(:,:,:,j),'valid') + b2(j);
        end
        
        %ReLU
        R2 = C2;
        R2(find(R2<0)) = 0;
        P2 = zeros(4,4,16);
        
        index = 1;
        F1 = zeros(1,16*4*4);
        %max polling P2
        for q = 1:16
            for u = 1:4
                for v = 1:4
                    a = sort(sort(R2(2*u-1:2*u,2*v-1:2*v,q),1,'descend'),2,'descend');
                    P2(u,v,q) = a(1,1);
                    % FC 1
                    F1(index) = P2(u,v,q);
                    index = index + 1;
                end
            end
        end
        
        %FC 2
        F2 = w1*F1' + bw1;
        F2 = 1./(1.+exp(-(F2)));
        
        %FC 3
        F3 = 1./(1.+exp(-((w2*F2) + bw2)));
        
        %FC 4
        F4 = w3*F3 + bw3;
        
        s = exp(F4)/sum(exp(F4));
        
        delY = s - Y';
        
        if i > 4000
            verr = verr + abs(dely.^2);
            continue;
        end
        
        err = err + abs(delY.^2);
        delbw3 = delY;
        delw3 = delY*F3';
        delF3 = w3'*delY;
        delbw2 = delF3.*F3.*(1-F3);
        delw2 = (delbw2)*F2';
        delF2 = w2'*(delbw2);
        delbw1 = delF2.*F2.*(1-F2);
        delw1 = (delbw1)*F1;
        delF1 = (w1'*(delbw1))';
        
        %delta P2
        index = 1;
        for q = 1:16
            for u = 1:4
                for v = 1:4
                    delP2(u,v,q) = delF1(index);
                    index = index + 1;
                end
            end
        end
        
        
        delR2 = zeros(size(R2));
        for q = 1:16
            for u = 1:4
                for v = 1:4
                    b = R2(2*u-1:2*u,2*v-1:2*v,q);
                    b(find(b~=P2(u,v))) = 0;
                    b(find(b==P2(u,v))) = 1;
                    delR2(2*u-1:2*u,2*v-1:2*v,q) = b*delP2(u,v);
                end
            end
        end
        
        
        
        
        delC2 = delR2;
        delC2(find(R2<0)) = 0;
        
        
        delb2 = zeros(size(b2));
        for q = 1:16
            delb2(q) = sum(sum(delC2(:,:,q)));
        end
        
        
        
        
        delk2 = zeros(size(k2));
        %delta k2
        for q = 1:16
            delk2(:,:,:,q) = convn(P1,delC2(:,:,q),'valid');
        end
        
        
        
        delP1 = zeros(size(P1));
        %delta P1
        for q = 1:16
            delP1 = delP1 + convn(delC2(:,:,q),rot90(rot90(k2(:,:,:,q))),'full');
        end
        
        
        
        %delta R1
        delR1 = zeros(size(R1));
        for q = 1:6
            for u = 1:12
                for v = 1:12
                    b = R1(2*u-1:2*u,2*v-1:2*v,q);
                    b(find(b~=P1(u,v))) = 0;
                    b(find(b==P1(u,v))) = 1;
                    delR1(2*u-1:2*u,2*v-1:2*v,q) = b*delP1(u,v);
                end
            end
        end
        
        
        
        %delta C1
        delC1 = delR1;
        delC1(find(R1<0)) = 0; 
        delb1 = zeros(size(b1));
        for q = 1:6
        delb1(q) = sum(sum(delC1(:,:,q)));
        end
        
        %delta k1
        delk1 = zeros(size(k1));
        for q = 1:6
            delk1(:,:,q) = convn(I,delC1(:,:,q),'valid');
        end
        
        
        
        dk1 = (decay*dk1) + ((1-decay)*(delk1.^2));
        dk2 = (decay*dk2) + ((1-decay)*(delk2.^2));
        dw1 = (decay*dw1) + ((1-decay)*(delw1.^2));
        dw2 = (decay*dw2) + ((1-decay)*(delw2.^2));
        dw3 = (decay*dw3) + ((1-decay)*(delw3.^2));
        db1 = (decay*db1) + ((1-decay)*(delb1.^2));
        db2 = (decay*db2) + ((1-decay)*(delb2.^2));
        dbw1 = (decay*dbw1) + ((1-decay)*(delbw1.^2));
        dbw2 = (decay*dbw2) + ((1-decay)*(delbw2.^2));
        dbw3 = (decay*dbw3) + ((1-decay)*(delbw3.^2));
        
        
        k1 = k1 - eta*(((dk1 + eps).^-0.5).*delk1);
        k2 = k2 - eta*(((dk2 + eps).^-0.5).*delk2);
        w1 = w1 - eta*(((dw1 + eps).^-0.5).*delw1);
        w2 = w2 - eta*(((dw2 + eps).^-0.5).*delw2);
        w3 = w3 - eta*(((dw3 + eps).^-0.5).*delw3);
        b1 = b1 - eta*(((db1 + eps).^-0.5).*delb1);
        b2 = b2 - eta*(((db2 + eps).^-0.5).*delb2);
        bw1 = bw1 - eta*(((dbw1 + eps).^-0.5).*delbw1);
        bw2 = bw2 - eta*(((dbw2 + eps).^-0.5).*delbw2);
        bw3 = bw3 - eta*(((dbw3 + eps).^-0.5).*delbw3);
    end
    
    errorr(epoch) = err;
    verrorr(epoch) = verr;
    
    disp(['k1',find(k1 == Inf),find(isnan(k1))
        'k2',find(k2 == Inf)
        ,find(isnan(k2))
        'w1',find(w1 == Inf)
        ,find(isnan(w1))
        'w2',find(w2 == Inf)
        ,find(isnan(w2))])
    err/size(img,4)
    save('variables4.mat','k1','k2','w1','w2','w3','b1','b2','bw1','bw2','bw3');
end
pause;
save('variablese.mat','k1','k2','w1','w2','w3','b1','b2','bw1','bw2','bw3','iporder');

