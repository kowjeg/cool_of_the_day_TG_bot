package ru.saveldu.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "stats")
public class Stat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "chat_id", nullable = false)
    private Long chatId;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Size(max = 255)
    @NotNull
    @Column(name = "user_name", nullable = false)
    private String userName;

    @NotNull
    @Column(name = "year", nullable = false)
    private Integer year;

    @NotNull
    @Column(name = "count", nullable = false)
    private Integer count;

    public Stat() {
    }

    public Integer getId() {
        return this.id;
    }

    public @NotNull Long getChatId() {
        return this.chatId;
    }

    public @NotNull Long getUserId() {
        return this.userId;
    }

    public @Size(max = 255) @NotNull String getUserName() {
        return this.userName;
    }

    public @NotNull Integer getYear() {
        return this.year;
    }

    public @NotNull Integer getCount() {
        return this.count;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setChatId(@NotNull Long chatId) {
        this.chatId = chatId;
    }

    public void setUserId(@NotNull Long userId) {
        this.userId = userId;
    }

    public void setUserName(@Size(max = 255) @NotNull String userName) {
        this.userName = userName;
    }

    public void setYear(@NotNull Integer year) {
        this.year = year;
    }

    public void setCount(@NotNull Integer count) {
        this.count = count;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Stat)) return false;
        final Stat other = (Stat) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$chatId = this.getChatId();
        final Object other$chatId = other.getChatId();
        if (this$chatId == null ? other$chatId != null : !this$chatId.equals(other$chatId)) return false;
        final Object this$userId = this.getUserId();
        final Object other$userId = other.getUserId();
        if (this$userId == null ? other$userId != null : !this$userId.equals(other$userId)) return false;
        final Object this$userName = this.getUserName();
        final Object other$userName = other.getUserName();
        if (this$userName == null ? other$userName != null : !this$userName.equals(other$userName)) return false;
        final Object this$year = this.getYear();
        final Object other$year = other.getYear();
        if (this$year == null ? other$year != null : !this$year.equals(other$year)) return false;
        final Object this$count = this.getCount();
        final Object other$count = other.getCount();
        if (this$count == null ? other$count != null : !this$count.equals(other$count)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Stat;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $chatId = this.getChatId();
        result = result * PRIME + ($chatId == null ? 43 : $chatId.hashCode());
        final Object $userId = this.getUserId();
        result = result * PRIME + ($userId == null ? 43 : $userId.hashCode());
        final Object $userName = this.getUserName();
        result = result * PRIME + ($userName == null ? 43 : $userName.hashCode());
        final Object $year = this.getYear();
        result = result * PRIME + ($year == null ? 43 : $year.hashCode());
        final Object $count = this.getCount();
        result = result * PRIME + ($count == null ? 43 : $count.hashCode());
        return result;
    }

    public String toString() {
        return "Stat(id=" + this.getId() + ", chatId=" + this.getChatId() + ", userId=" + this.getUserId() + ", userName=" + this.getUserName() + ", year=" + this.getYear() + ", count=" + this.getCount() + ")";
    }
}