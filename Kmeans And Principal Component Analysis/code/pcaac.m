clear all
load('imgsnlabels.mat','imgs','labels');
i = 0;
err = 1;

means = sum(imgs,2)/400;
size(means)
stdim = imgs - repmat(means,1,400);

size(stdim)
covr = (1/5000)*(stdim'*stdim);
%load('cvr.mat','covr');
size(covr)
[U,S,V] = svd(covr);

while(abs(err)>0)
    i = i + 1;
    red = U(:,1:i);
    Z = stdim*red;  % transformed matrix
    recon = Z*(red');  % Reconstruction matrix
    recon = recon + repmat(means,1,400);
    err = 0;
    ep = 0;
    errr = [];
    for j = 1:400
        errr(j) = norm(recon(j,:)-imgs(j,:))^2;
    end
    errmean(i) = mean(errr);
    err = mean(errr)
end

fprintf('pca at error < 0.1 is %d\n',i);
save('errmean.mat','errmean');