package com.cms.dto;

import java.util.Map;

public class ConsultantStatsDto {
    private long totalConsultants;
    private long activeConsultants;
    private long availableConsultants;
    private long onProjectConsultants;
    private long inactiveConsultants;
    private long newThisMonth;
    private int totalTechnologies;
    private Map<String, Long> technologyDistribution;
    private Map<String, Long> statusDistribution;

    public ConsultantStatsDto() {
    }

    public ConsultantStatsDto(long totalConsultants, long activeConsultants, long availableConsultants, long onProjectConsultants, long inactiveConsultants, long newThisMonth, int totalTechnologies, Map<String, Long> technologyDistribution, Map<String, Long> statusDistribution) {
        this.totalConsultants = totalConsultants;
        this.activeConsultants = activeConsultants;
        this.availableConsultants = availableConsultants;
        this.onProjectConsultants = onProjectConsultants;
        this.inactiveConsultants = inactiveConsultants;
        this.newThisMonth = newThisMonth;
        this.totalTechnologies = totalTechnologies;
        this.technologyDistribution = technologyDistribution;
        this.statusDistribution = statusDistribution;
    }

    public long getTotalConsultants() {
        return totalConsultants;
    }

    public void setTotalConsultants(long totalConsultants) {
        this.totalConsultants = totalConsultants;
    }

    public long getActiveConsultants() {
        return activeConsultants;
    }

    public void setActiveConsultants(long activeConsultants) {
        this.activeConsultants = activeConsultants;
    }

    public long getAvailableConsultants() {
        return availableConsultants;
    }

    public void setAvailableConsultants(long availableConsultants) {
        this.availableConsultants = availableConsultants;
    }

    public long getOnProjectConsultants() {
        return onProjectConsultants;
    }

    public void setOnProjectConsultants(long onProjectConsultants) {
        this.onProjectConsultants = onProjectConsultants;
    }

    public long getInactiveConsultants() {
        return inactiveConsultants;
    }

    public void setInactiveConsultants(long inactiveConsultants) {
        this.inactiveConsultants = inactiveConsultants;
    }

    public long getNewThisMonth() {
        return newThisMonth;
    }

    public void setNewThisMonth(long newThisMonth) {
        this.newThisMonth = newThisMonth;
    }

    public int getTotalTechnologies() {
        return totalTechnologies;
    }

    public void setTotalTechnologies(int totalTechnologies) {
        this.totalTechnologies = totalTechnologies;
    }

    public Map<String, Long> getTechnologyDistribution() {
        return technologyDistribution;
    }

    public void setTechnologyDistribution(Map<String, Long> technologyDistribution) {
        this.technologyDistribution = technologyDistribution;
    }

    public Map<String, Long> getStatusDistribution() {
        return statusDistribution;
    }

    public void setStatusDistribution(Map<String, Long> statusDistribution) {
        this.statusDistribution = statusDistribution;
    }

    public static ConsultantStatsDtoBuilder builder() {
        return new ConsultantStatsDtoBuilder();
    }

    public static class ConsultantStatsDtoBuilder {
        private long totalConsultants;
        private long activeConsultants;
        private long availableConsultants;
        private long onProjectConsultants;
        private long inactiveConsultants;
        private long newThisMonth;
        private int totalTechnologies;
        private Map<String, Long> technologyDistribution;
        private Map<String, Long> statusDistribution;

        public ConsultantStatsDtoBuilder totalConsultants(long totalConsultants) {
            this.totalConsultants = totalConsultants;
            return this;
        }

        public ConsultantStatsDtoBuilder activeConsultants(long activeConsultants) {
            this.activeConsultants = activeConsultants;
            return this;
        }

        public ConsultantStatsDtoBuilder availableConsultants(long availableConsultants) {
            this.availableConsultants = availableConsultants;
            return this;
        }

        public ConsultantStatsDtoBuilder onProjectConsultants(long onProjectConsultants) {
            this.onProjectConsultants = onProjectConsultants;
            return this;
        }

        public ConsultantStatsDtoBuilder inactiveConsultants(long inactiveConsultants) {
            this.inactiveConsultants = inactiveConsultants;
            return this;
        }

        public ConsultantStatsDtoBuilder newThisMonth(long newThisMonth) {
            this.newThisMonth = newThisMonth;
            return this;
        }

        public ConsultantStatsDtoBuilder totalTechnologies(int totalTechnologies) {
            this.totalTechnologies = totalTechnologies;
            return this;
        }

        public ConsultantStatsDtoBuilder technologyDistribution(Map<String, Long> technologyDistribution) {
            this.technologyDistribution = technologyDistribution;
            return this;
        }

        public ConsultantStatsDtoBuilder statusDistribution(Map<String, Long> statusDistribution) {
            this.statusDistribution = statusDistribution;
            return this;
        }

        public ConsultantStatsDto build() {
            return new ConsultantStatsDto(totalConsultants, activeConsultants, availableConsultants, onProjectConsultants, inactiveConsultants, newThisMonth, totalTechnologies, technologyDistribution, statusDistribution);
        }
    }
}
