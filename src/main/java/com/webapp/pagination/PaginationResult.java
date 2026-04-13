package com.webapp.pagination;

import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;

public class PaginationResult<E> {


    private int totalRecords;
    private int currentPage;
    private List<E> buildingList;
    private int maxResult;
    private int totalPages;
    private int maxNavigationPage;
    private List<Integer> navigationPages;

    public PaginationResult(List<E> buildingList, int totalRecords, int page, int maxResult, int maxNavigationPage) {
        this.buildingList = buildingList;
        this.totalRecords = totalRecords;
        this.currentPage = Math.max(page, 1);
        this.maxResult = maxResult;
        this.totalPages = (int) Math.ceil((double) totalRecords / maxResult);
        this.maxNavigationPage = Math.min(maxNavigationPage, totalPages);
        calcNavigationPages();
    }

    public PaginationResult(TypedQuery<E> query, TypedQuery<Long> countQuery, int page, int maxResult, int maxNavigationPage) {

        this.maxResult = maxResult;
        this.currentPage = Math.max(page, 1);

        // 1. Đếm số bản ghi
        this.totalRecords = countQuery.getSingleResult().intValue();

        // 2. Tính tổng số trang
        this.totalPages = (int) Math.ceil((double) totalRecords / maxResult);

        // 3. Lấy dữ liệu phân trang
        this.buildingList = query.setFirstResult((currentPage - 1) * maxResult).setMaxResults(maxResult).getResultList();

        // 4. Tính navigation
        this.maxNavigationPage = Math.min(maxNavigationPage, totalPages);
        calcNavigationPages();
    }

    private void calcNavigationPages() {
        navigationPages = new ArrayList<>();

        int current = Math.min(currentPage, totalPages);

        int begin = current - maxNavigationPage / 2;
        int end = current + maxNavigationPage / 2;

        navigationPages.add(1);

        if (begin > 2) navigationPages.add(-1);

        for (int i = begin; i <= end; i++) {
            if (i > 1 && i < totalPages) {
                navigationPages.add(i);
            }
        }

        if (end < totalPages - 2) navigationPages.add(-1);

        if (totalPages > 1) navigationPages.add(totalPages);
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

    public List<E> getBuildingList() {
        return buildingList;
    }

    public int getMaxResult() {
        return maxResult;
    }

    public List<Integer> getNavigationPages() {
        return navigationPages;
    }
}
