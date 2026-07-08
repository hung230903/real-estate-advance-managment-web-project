package com.webapp.pagination;

import java.util.ArrayList;
import java.util.List;

public class PaginationResult<E> {

  private int totalRecords;
  private int currentPage;
  private List<E> entityList;
  private int maxResult;
  private int totalPages;
  private int maxNavigationPage;
  private List<Integer> navigationPages;

  public PaginationResult(List<E> entityList, int totalRecords, int page, int maxResult, int maxNavigationPage) {
    this.entityList = entityList;
    this.totalRecords = totalRecords;
    this.maxResult = maxResult;
    this.totalPages = (int) Math.ceil((double) totalRecords / maxResult);
    this.currentPage = (page > 1 && page > this.totalPages) ? 1 : Math.max(page, 1);
    this.maxNavigationPage = Math.min(maxNavigationPage, totalPages);
    calcNavigationPages();
  }

  private void calcNavigationPages() {
    navigationPages = new ArrayList<>();

    int current = Math.min(currentPage, totalPages);

    int begin = current - maxNavigationPage / 2;
    int end = current + maxNavigationPage / 2;

    navigationPages.add(1);

    if (begin > 2)
      navigationPages.add(-1);

    for (int i = begin; i <= end; i++) {
      if (i > 1 && i < totalPages) {
        navigationPages.add(i);
      }
    }

    if (end < totalPages - 2)
      navigationPages.add(-1);

    if (totalPages > 1)
      navigationPages.add(totalPages);
  }

  public int getTotalPages() {
    return totalPages;
  }

  public int getTotalRecords() {
    return totalRecords;
  }

  public int getCurrentPage() {
    return currentPage;
  }

  public List<E> getEntityList() {
    return entityList;
  }

  public int getMaxResult() {
    return maxResult;
  }

  public List<Integer> getNavigationPages() {
    return navigationPages;
  }
}
