package inha.git.statistics.api.service;

import inha.git.user.domain.User;

public interface StatisticsService {

    void increaseCount(User user, Integer type);

    void decreaseCount(User user, Integer type);
}
