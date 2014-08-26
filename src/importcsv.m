
data = dlmread("/Users/toshihirokuboi/Workspace/eDetection_v2_1/src/eyeCandidatesinfo1.txt",",");
data = data(2:end,:);
posdata = data(data(:,2)==1,:); 
negdata = data(data(:,2)==0,:);
mean_pos = mean(posdata);
mean_neg = mean(negdata);
disp (mean_pos(17:26));
disp (mean_neg(17:26));
x=1:1:10;
figure (1)
subplot (3, 3, 1)
plot(mean_pos(17:26),"r",mean_neg(17:26),"b");
title("(a) sumHistH");

subplot (3, 3, 2)
plot(mean_pos(27:36),"r",mean_neg(27:36),"b");
title("(b) sumHistV");

subplot (3, 3, 3)
plot(mean_pos(37:60),"r",mean_neg(37:60),"b");
title("(c) sumHistR");

subplot (3, 3, 4)
%plot(mean_pos(17:26) ./ sum(mean_pos(17:26)),"r",mean_neg(17:26) ./ sum(mean_neg(17:26)),"b");
%plot(mean_pos(27:36) ./ sum(mean_pos(27:36)),"r",mean_neg(27:36) ./ sum(mean_neg(27:36)),"b");
plot(mean_pos(61:70),"r",mean_neg(61:70),"b");
title("(d) diffHistH");

subplot (3, 3, 5)
plot(mean_pos(71:80),"r",mean_neg(71:80),"b");
title("(e) diffHistV");

subplot (3, 3, 6)
plot(mean_pos(81:104),"r",mean_neg(81:104),"b");
title("(f) diffHistR");


%plot(mean_pos(71:80) ./ sum(data(71:80)),"r",mean_neg((71:80) ./ sum(data(:36)),"b");

subplot (3, 3, 7)
plot(mean_pos(199:208),"r",mean_neg(199:208),"b");
title("(g) atanHistH");

subplot (3, 3, 8)
plot(mean_pos(209:218),"r",mean_neg(209:218),"b");
title("(h) atanHistV");

subplot (3, 3, 9)
plot(mean_pos(105:204),"r",mean_neg(105:204),"b");
title("(i) edgeMap");
